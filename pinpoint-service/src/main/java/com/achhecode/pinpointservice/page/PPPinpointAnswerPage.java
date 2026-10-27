
package com.achhecode.pinpointservice.page;

import com.achhecode.browserpilotdto.dto.PinpointResponse;
import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.pinpointservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class PPPinpointAnswerPage extends WebsitePage implements AnswerPage<PinpointResponse> {

    private static final String LATEST_ANSWER_LINK_XPATH = "//a[contains(@href, '/linkedin-pinpoint-answer')"
            + " and contains(normalize-space(.), 'Latest Pinpoint Answer')]";

    private static final String REVEAL_ANSWER_BUTTON_XPATH = "//button[contains(normalize-space(.), 'Reveal answer & analysis')]";

    private static final String ANSWER_HEADING_SELECTOR = "h2[data-copy='answer']";

    private static final String PINPOINT_ANSWER_URL_PREFIX = "https://www.linkedinpinpointanswer.today/linkedin-pinpoint-answer/pinpoint-";

    public PPPinpointAnswerPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().contains(PinpointanswerUrls.PINPOINT);
    }

    @Override
    public PinpointResponse getAnswer() {
        navigateToLatestAnswer();
        revealAnswer();

        return new PinpointResponse(extractAnswerText());
    }

    private void navigateToLatestAnswer() {
        Locator latestAnswerLink = page.locator(LATEST_ANSWER_LINK_XPATH);

        latestAnswerLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // Click first: this triggers navigation.
        latestAnswerLink.click();

        // Wait until the resulting URL matches the expected pattern.
        page.waitForURL("**/linkedin-pinpoint-answer/pinpoint-*", 
        new Page.WaitForURLOptions().setTimeout(5_000));

        if (!page.url().startsWith(PINPOINT_ANSWER_URL_PREFIX)) {
            throw new IllegalStateException("Unexpected Pinpoint answer URL: " + page.url());
        }
    }

    /**
     * Clicks the reveal button when it is present and visible.
     */
    private void revealAnswer() {
        Locator revealButton = page.locator(REVEAL_ANSWER_BUTTON_XPATH).first();

        revealButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        revealButton.click();

        waitForAnswerHeading();
    }

    /**
     * Waits until the answer heading is visible.
     */
    private void waitForAnswerHeading() {
        page.locator(ANSWER_HEADING_SELECTOR)
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    /**
     * Extracts the revealed answer text.
     */
    private String extractAnswerText() {
        Locator answerHeading = page.locator(ANSWER_HEADING_SELECTOR);
        waitForAnswerHeading();

        String answer = answerHeading.innerText().trim();

        if (answer.isEmpty()) {
            throw new IllegalStateException("The Pinpoint answer heading is empty.");
        }

        return answer;
    }
}
