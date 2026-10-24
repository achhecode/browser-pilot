package com.achhecode.instahyreservice.service;

import com.achhecode.instahyreservice.InstahyrePageResolver;
import com.achhecode.instahyreservice.InstahyreProperties;
import com.achhecode.instahyreservice.model.JobApplicationRequest;
import com.achhecode.instahyreservice.page.LoginPage;
import com.achhecode.instahyreservice.page.OpportunitiesPage;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstahyreAutomationService {

    private final InstahyrePageResolver pageResolver;
    private final InstahyreProperties properties;

    public void login() {
        Page page = pageResolver.findPage();

        LoginPage page2 = new LoginPage(page);

        page.waitForURL(
                url -> url.contains("/login"),
                new Page.WaitForURLOptions().setTimeout(10_000)
        );

        if (properties.username() == null || properties.username().isBlank()
                || properties.password() == null || properties.password().isBlank()) {
            throw new IllegalStateException(
                    "Instahyre username or password is not configured."
            );
        }

        page2.login(
                properties.username(),
                properties.password()
        );
    }


    public void applyJob(JobApplicationRequest request) {
        Page page = pageResolver.findPage();

        OpportunitiesPage page2 = new OpportunitiesPage(page);

        page2.clickApply(request);
        log.info("Applied jobs");
    }
}
