package com.achhecode.instahyreservice.page;

import com.achhecode.instahyreservice.InstahyreUrls;
import com.achhecode.instahyreservice.matching.JobMatcher;
import com.achhecode.instahyreservice.model.JobApplicationRequest;
import com.achhecode.instahyreservice.model.JobDetails;
import com.achhecode.playwrightservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Inspects Instahyre opportunities and applies to the ones matching a {@link JobApplicationRequest}.
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
    /** Safety cap so a request that never reaches noOfJobs cannot loop forever. */
    private static final int MAX_JOBS_TO_INSPECT = 200;

    private static final double ELEMENT_TIMEOUT_MS = 10_000;
    private static final double PAGE_LOAD_TIMEOUT_MS = 15_000;
    private static final double APPLY_ALL_PROMPT_TIMEOUT_MS = 3_000; // popup is optional, keep short
    private static final long JOB_LOAD_TIMEOUT_MS = 10_000;
    private static final double POLL_INTERVAL_MS = 200;

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

        static final String COMPANY_NAME =
                "//div[@id='employer-profile']//*[contains(@class,'company-name')]";

        static final String COMPANY_SIZE =
                "//div[@id='employer-profile']//*[contains(@class,'company-size')]";

        static final String APPLY_BUTTON =
                "xpath=//div[contains(@class,'apply')]" +
                "//button[contains(@class,'new-btn') and normalize-space()='Apply']";

        // Optional popup shown after Apply when the same company has more jobs.
        static final String APPLY_ALL_MODAL =
                "xpath=//div[contains(@class,'app-modal') and contains(@class,'apply-all-modal')]";

        // static final String APPLY_ALL_BUTTON = "xpath=//button[@ng-click='applyBulk()']";
        
        static final String CANCEL_ALL_BUTTON = "xpath=//button[@ng-click='applyBulkCancel()']";

        // Verified in Chrome DevTools: exactly 1 match, visible, enabled, not covered.
        static final String NOT_INTERESTED_BUTTON =
                "button.decline.new-btn:has-text('Not interested')";

        private Selectors() {}
    }

    // ---------- Types ----------
    private enum Outcome { APPLIED, DECLINED, FAILED }

    private record Summary(int applied, int declined, int inspected) {}

    private final Page page;

    public OpportunitiesPage(Page page) {
        super(page);
        this.page = page;
    }

    // ---------- Public API ----------

    public void clickApply(JobApplicationRequest request) {
        if (!isOnOpportunitiesPage()) {
            log.warn("Not on the opportunities page (current URL: {}). Aborting.", page.url());
            return;
        }

        log.info("Starting with request: {}", request);

        Summary summary = new Summary(0, 0, 0);
        try {
            summary = processJobs(request);
        } catch (PlaywrightException e) {
            log.error("Unexpected Playwright error: {}", e.getMessage());
        }

        log.info("Finished. Applied: {}/{}, Not interested: {}, Inspected: {}",
                summary.applied(), request.noOfJobs(), summary.declined(), summary.inspected());
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

    private Summary processJobs(JobApplicationRequest request) {
        JobMatcher matcher = new JobMatcher(request);
        int applied = 0;
        int declined = 0;
        int inspected = 0;

        Optional<JobDetails> current = openJobIfNeeded();

        while (current.isPresent()
                && applied < request.noOfJobs()
                && inspected < MAX_JOBS_TO_INSPECT) {

            inspected++;
            log.info("========== Job {} (applied {}/{}) ==========",
                    inspected, applied, request.noOfJobs());

            JobDetails job = current.get();
            logJob(job);

            Outcome outcome = processJob(job, matcher);
            if (outcome == Outcome.FAILED) {
                break;
            }

            if (outcome == Outcome.APPLIED) {
                applied++;
            } else {
                declined++;
            }

            current = applied < request.noOfJobs() ? awaitNextJob(job) : Optional.empty();
        }

        return new Summary(applied, declined, inspected);
    }

    private Outcome processJob(JobDetails job, JobMatcher matcher) {
        if (!matcher.hasRequiredFields(job)) {
            log.error("Request filters on company name/size but they could not be read. "
                    + "Check the COMPANY_* selectors. Stopping without clicking anything.");
            return Outcome.FAILED;
        }

        if (matcher.matches(job)) {
            return apply() ? Outcome.APPLIED : Outcome.FAILED;
        }
        return notInterested() ? Outcome.DECLINED : Outcome.FAILED;
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
        log.info("Criteria matched. Applying...");

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
        // if (!clickWhenReady(Selectors.APPLY_ALL_BUTTON, "Apply all")) {
        //     return false;
        // }

        if (!clickWhenReady(Selectors.CANCEL_ALL_BUTTON, "Cancel")) {
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
                getText(Selectors.JOB_DESCRIPTION),
                getText(Selectors.COMPANY_NAME),
                getText(Selectors.COMPANY_SIZE)
        );
    }

    private void logJob(JobDetails job) {
        log.info("Job title: {}", job.title());
        log.info("Company: {} (size: {})", job.companyName(), job.companySize());
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

    public boolean isDisplayed(){
        return page.url().contains(InstahyreUrls.OPPORTUNITIES);
    }
}