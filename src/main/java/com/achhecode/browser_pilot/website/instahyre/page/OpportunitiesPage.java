package com.achhecode.browser_pilot.website.instahyre.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Inspects Instahyre opportunities and applies to the ones matching the criteria.
 *
 * The job modal stays open: after Apply / Not interested, the next job is
 * loaded into the same modal, so we wait for its content to change instead of
 * re-opening rows.
 *
 * Requires Java 16+ (records).
 */
@Slf4j
public class OpportunitiesPage extends WebsitePage {

    // ---------- Configuration ----------
    private static final int MAX_ATTEMPTS = 2;
    private static final double ELEMENT_TIMEOUT_MS = 10_000;
    private static final double PAGE_LOAD_TIMEOUT_MS = 15_000;
    private static final double APPLY_ALL_PROMPT_TIMEOUT_MS = 3_000; // popup is optional, keep short
    private static final long JOB_LOAD_TIMEOUT_MS = 10_000;
    private static final double POLL_INTERVAL_MS = 200;

    private static final String REQUIRED_KEYWORD = "Java";
    private static final String REQUIRED_DESCRIPTION_TERM = "Spring";

    // Matches /candidate/opportunities/ with or without a query string (e.g. ?matching=true).
    private static final String OPPORTUNITIES_URL_PREFIX =
            "https://www.instahyre.com/candidate/opportunities/";
    private static final String OPPORTUNITIES_URL_GLOB = OPPORTUNITIES_URL_PREFIX + "**";

    // ---------- Selectors ----------
    private static final class Selectors {
        static final String EMPLOYER_ROWS = "div.employer-row";
        static final String VIEW_JOB_BUTTON = "button[type='button'].btn-interested";

        static final String MODAL = "//div[@id='employer-profile']";

        static final String JOB_TITLE = "//*[@id='job-description']/h2";

        static final String JOB_FUNCTION =
                "//*[@id='job-description']" +
                "//div[contains(@class,'job-desc-subheading')]" +
                "//span[contains(@class,'ng-binding')]";

        static final String JOB_KEYWORDS = "//*[@id='job-skills-description']/li";

        static final String JOB_DESCRIPTION =
                "//*[@id='job-description']" +
                "//div[contains(@class,'job-description')]" +
                "//p[@ng-bind-html]";

        static final String APPLY_BUTTON =
                "xpath=//div[contains(@class,'apply')]" +
                "//button[contains(@class,'new-btn') and normalize-space()='Apply']";

        // Optional popup shown after Apply when the same company has more jobs.
        static final String APPLY_ALL_MODAL =
                "xpath=//div[contains(@class,'app-modal') and contains(@class,'apply-all-modal')]";

        static final String APPLY_ALL_BUTTON = "xpath=//button[@ng-click='applyBulk()']";

        // Verified in Chrome DevTools: exactly 1 match, visible, enabled, not covered.
        static final String NOT_INTERESTED_BUTTON =
                "button.decline.new-btn:has-text('Not interested')";

        private Selectors() {}
    }

    // ---------- Types ----------
    private record JobDetails(String title, String function, String keywords, String description) {
        boolean isLoaded() {
            return !title.isBlank() && !description.isBlank();
        }
    }

    private enum Outcome { APPLIED, DECLINED, FAILED }

    private final Page page;

    public OpportunitiesPage(Page page) {
        super(page);
        this.page = page;
    }

    // ---------- Public API ----------

    public void clickApply() {
        if (!isOnOpportunitiesPage()) {
            log.warn("Not on the opportunities page (current URL: {}). Aborting.", page.url());
            return;
        }

        int applied = 0;
        int declined = 0;

        try {
            Optional<JobDetails> current = openJobIfNeeded();

            for (int attempt = 1; attempt <= MAX_ATTEMPTS && current.isPresent(); attempt++) {
                log.info("========== Attempt {} of {} ==========", attempt, MAX_ATTEMPTS);

                JobDetails job = current.get();
                logJob(job);

                Outcome outcome = processJob(job);
                if (outcome == Outcome.FAILED) {
                    break;
                }

                switch (outcome) {
                    case APPLIED -> applied++;
                    case DECLINED -> declined++;
                    default -> { }
                }

                if (attempt < MAX_ATTEMPTS) {
                    current = awaitNextJob(job);
                }
            }
        } catch (PlaywrightException e) {
            log.error("Unexpected Playwright error: {}", e.getMessage());
        }

        log.info("Finished. Applied: {}, Not interested: {}", applied, declined);
    }

    // ---------- Page verification ----------

    /** True when the URL starts with https://www.instahyre.com/candidate/opportunities/ */
    public boolean isOnOpportunitiesPage() {
        try {
            // Waits briefly in case a redirect / navigation is still in progress.
            page.waitForURL(
                    OPPORTUNITIES_URL_GLOB,
                    new Page.WaitForURLOptions().setTimeout(PAGE_LOAD_TIMEOUT_MS));
            return page.url().startsWith(OPPORTUNITIES_URL_PREFIX);
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // ---------- Orchestration ----------

    private Outcome processJob(JobDetails job) {
        if (matchesCriteria(job)) {
            return apply() ? Outcome.APPLIED : Outcome.FAILED;
        }
        return notInterested() ? Outcome.DECLINED : Outcome.FAILED;
    }

    private boolean matchesCriteria(JobDetails job) {
        return containsIgnoreCase(job.keywords(), REQUIRED_KEYWORD)
                && containsIgnoreCase(job.description(), REQUIRED_DESCRIPTION_TERM);
    }

    // ---------- Opening a job ----------

    /** Reuses the modal if it is already open, otherwise opens the first listed job. */
    @SuppressWarnings("null")
    private Optional<JobDetails> openJobIfNeeded() {
        if (isModalVisible()) {
            log.info("Job modal is already open. Skipping 'View job' click.");
            return awaitJob(JobDetails::isLoaded);
        }
        return openFirstJob();
    }

    private boolean isModalVisible() {
        return page.locator(Selectors.MODAL).first().isVisible();
    }

    @SuppressWarnings("null")
private Optional<JobDetails> openFirstJob() {
        Locator viewJobButton = page.locator(Selectors.EMPLOYER_ROWS)
                .first()
                .locator(Selectors.VIEW_JOB_BUTTON)
                .first();

        if (viewJobButton.count() == 0) {
            log.info("No job to open. Stopping.");
            return Optional.empty();
        }

        viewJobButton.click();

        if (!waitForModalVisible()) {
            return Optional.empty();
        }
        return awaitJob(JobDetails::isLoaded);
    }

    private boolean waitForModalVisible() {
        boolean visible = waitForState(
                Selectors.MODAL, WaitForSelectorState.VISIBLE, ELEMENT_TIMEOUT_MS);

        if (visible) {
            log.info("Job modal loaded.");
        } else {
            log.warn("Job modal did not become visible.");
        }
        return visible;
    }

    /** Waits for the element to reach the given state; false on timeout. */
    private boolean waitForState(String selector, WaitForSelectorState state, double timeoutMs) {
        try {
            page.locator(selector).first().waitFor(
                    new Locator.WaitForOptions().setState(state).setTimeout(timeoutMs));
            return true;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // ---------- Actions ----------

    private boolean apply() {
        log.info("{} and {} matched. Applying...", REQUIRED_KEYWORD, REQUIRED_DESCRIPTION_TERM);
        if (!clickWhenReady(Selectors.APPLY_BUTTON, "Apply")) {
            return false;
        }
        return handleApplyAllPrompt();
    }

    /**
     * After Apply, Instahyre may offer to apply to every job of the same company.
     * If that popup shows up, confirm it; the job modal then loads the next job.
     *
     * @return false only if the popup appeared but could not be confirmed
     */
    private boolean handleApplyAllPrompt() {
        boolean promptShown = waitForState(
                Selectors.APPLY_ALL_MODAL, WaitForSelectorState.VISIBLE, APPLY_ALL_PROMPT_TIMEOUT_MS);

        if (!promptShown) {
            log.info("No 'apply to all' prompt appeared.");
            return true;
        }

        log.info("'Apply to all' prompt detected. Confirming...");
        if (!clickWhenReady(Selectors.APPLY_ALL_BUTTON, "Apply all")) {
            return false;
        }

        waitForState(Selectors.APPLY_ALL_MODAL, WaitForSelectorState.HIDDEN, ELEMENT_TIMEOUT_MS);
        return true;
    }

    private boolean notInterested() {
        log.info("Criteria not matched. Clicking Not interested...");
        return clickWhenReady(Selectors.NOT_INTERESTED_BUTTON, "Not interested");
    }

    /**
     * Clicks the first VISIBLE element matching the selector. click() auto-waits for
     * the element to be visible, stable and enabled, so no manual visibility check
     * (which races with the modal re-rendering) is needed.
     */
    private boolean clickWhenReady(String selector, String label) {
        Locator button = page.locator(selector + " >> visible=true").first();
        log.info("'{}' matches: {}", label, page.locator(selector).count());

        try {
            button.click(new Locator.ClickOptions().setTimeout(ELEMENT_TIMEOUT_MS));
            log.info("'{}' clicked.", label);
            return true;
        } catch (PlaywrightException e) {
            log.warn("Normal click failed for '{}': {}", label, e.getMessage());
            return dispatchClick(button, label);
        }
    }

    /** Fallback: fires the DOM click event directly (same as element.click() in the console). */
    private boolean dispatchClick(Locator button, String label) {
        try {
            button.dispatchEvent("click");
            log.info("'{}' clicked via dispatchEvent.", label);
            return true;
        } catch (PlaywrightException e) {
            log.warn("dispatchEvent click failed for '{}': {}", label, e.getMessage());
            return false;
        }
    }

    // ---------- Waiting for the modal content ----------

    /** Waits until the modal shows a fully loaded job different from the previous one. */
    private Optional<JobDetails> awaitNextJob(JobDetails previous) {
        log.info("Waiting for the next job to load in the modal...");
        return awaitJob(job -> job.isLoaded() && !job.equals(previous));
    }

    private Optional<JobDetails> awaitJob(Predicate<JobDetails> condition) {
        long deadline = System.currentTimeMillis() + JOB_LOAD_TIMEOUT_MS;

        while (System.currentTimeMillis() < deadline) {
            JobDetails job = readJobDetails();
            if (condition.test(job)) {
                return Optional.of(job);
            }
            page.waitForTimeout(POLL_INTERVAL_MS);
        }

        log.warn("Timed out waiting for job details. Stopping.");
        return Optional.empty();
    }

    // ---------- Job details ----------

    private JobDetails readJobDetails() {
        return new JobDetails(
                getText(Selectors.JOB_TITLE),
                getCombinedText(Selectors.JOB_FUNCTION),
                getCombinedText(Selectors.JOB_KEYWORDS),
                getText(Selectors.JOB_DESCRIPTION)
        );
    }

    private void logJob(JobDetails job) {
        log.info("Job title: {}", job.title());
        log.info("Job function: {}", job.function());
        log.info("Keywords: {}", job.keywords());
        log.info("Description: {}", job.description());
    }

    // ---------- Text helpers ----------

    private String getText(String xpath) {
        Locator locator = page.locator(xpath).first();
        return (locator.count() == 0 || !locator.isVisible())
                ? ""
                : locator.innerText().trim();
    }

    @SuppressWarnings("null")
private String getCombinedText(String xpath) {
        return page.locator(xpath)
                .allInnerTexts()
                .stream()
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.joining(", "));
    }

    private static boolean containsIgnoreCase(String text, String expected) {
        return text != null
                && text.toLowerCase(Locale.ROOT).contains(expected.toLowerCase(Locale.ROOT));
    }
}