package com.achhecode.instahyreservice.matching;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Locale;

import com.achhecode.instahyreservice.model.JobApplicationRequest;
import com.achhecode.instahyreservice.model.JobDetails;

/**
 * Decides whether a job satisfies a {@link JobApplicationRequest}. Empty
 * filters always pass.
 */
@RequiredArgsConstructor
public class JobMatcher {

    private final JobApplicationRequest request;

    public boolean matches(JobDetails job) {
        return containsAll(job.keywords(), request.keywords())
                && containsAll(job.description(), request.descriptionSkills())
                && containsIfRequested(job.companyName(), request.companyName())
                && containsIfRequested(job.companySize(), request.companySize());
    }

    /**
     * Guards against declining jobs just because a company field could not be read
     * (e.g. wrong selector): if the request filters on it, the page must provide
     * it.
     */
    public boolean hasRequiredFields(JobDetails job) {
        boolean nameOk = !request.hasCompanyName() || !job.companyName().isBlank();
        boolean sizeOk = !request.hasCompanySize() || !job.companySize().isBlank();
        return nameOk && sizeOk;
    }

    private static boolean containsAll(String text, List<String> terms) {
        return terms.stream().allMatch(term -> containsIgnoreCase(text, term));
    }

    private static boolean containsIfRequested(String text, String expected) {
        return expected.isEmpty() || containsIgnoreCase(text, expected);
    }

    private static boolean containsIgnoreCase(String text, String expected) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(expected.toLowerCase(Locale.ROOT));
    }
}