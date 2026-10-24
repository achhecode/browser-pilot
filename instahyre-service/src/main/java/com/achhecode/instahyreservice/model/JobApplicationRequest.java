package com.achhecode.instahyreservice.model;

import lombok.Builder;

import java.util.List;
import java.util.Objects;

/**
 * Describes which jobs to apply to.
 *
 * Every filter is optional: an empty list / blank text means "do not filter on this",
 * so a request with only {@code noOfJobs} applies to every job it sees.
 *
 * @param noOfJobs          how many jobs to apply to (must be > 0)
 * @param keywords          skills/keywords that must ALL appear in the job's keyword list
 * @param descriptionSkills terms that must ALL appear in the job description
 * @param companyName       text the company name must contain (blank = any)
 * @param companySize       text the company size must contain, e.g. "51-200" (blank = any)
 */
@Builder
public record JobApplicationRequest(
        int noOfJobs,
        List<String> keywords,
        List<String> descriptionSkills,
        String companyName,
        String companySize) {

    public JobApplicationRequest {
        if (noOfJobs <= 0) {
            throw new IllegalArgumentException("noOfJobs must be greater than 0, was " + noOfJobs);
        }
        keywords = clean(keywords);
        descriptionSkills = clean(descriptionSkills);
        companyName = normalize(companyName);
        companySize = normalize(companySize);
    }

    public boolean hasCompanyName() {
        return !companyName.isEmpty();
    }

    public boolean hasCompanySize() {
        return !companySize.isEmpty();
    }

    @SuppressWarnings("null")
    private static List<String> clean(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .distinct()
                .toList();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}