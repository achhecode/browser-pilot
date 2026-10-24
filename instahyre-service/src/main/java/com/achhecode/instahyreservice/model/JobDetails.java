package com.achhecode.instahyreservice.model;

/** Snapshot of the job currently shown in the job modal. */
public record JobDetails(
        String title,
        String function,
        String keywords,
        String description,
        String companyName,
        String companySize) {

    /** True once the modal has finished rendering a job. */
    public boolean isLoaded() {
        return !title.isBlank() && !description.isBlank();
    }
}