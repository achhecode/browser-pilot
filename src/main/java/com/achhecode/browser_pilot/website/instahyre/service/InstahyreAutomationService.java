package com.achhecode.browser_pilot.website.instahyre.service;

import com.achhecode.browser_pilot.website.instahyre.InstahyrePageFactory;
import com.achhecode.browser_pilot.website.instahyre.InstahyrePageResolver;
import com.achhecode.browser_pilot.website.instahyre.InstahyreProperties;
import com.achhecode.browser_pilot.website.instahyre.model.JobApplicationRequest;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstahyreAutomationService {

    private final InstahyrePageResolver pageResolver;
    private final InstahyrePageFactory pageFactory;
    private final InstahyreProperties properties;

    public void login() {
        Page page = pageResolver.findPage();

        pageFactory.home(page).clickLogin();
        log.info("Clicked login");

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

        pageFactory.login(page).login(
                properties.username(),
                properties.password()
        );
    }


    public void applyJob(JobApplicationRequest request) {
        Page page = pageResolver.findPage();

        pageFactory.opportunities(page).clickApply(request);
        log.info("Applied jobs");
    }

    public void signup() {
        Page page = pageResolver.findPage();

        pageFactory.home(page).clickSignup();
    }

    public void openFreshTab() {
        pageResolver.openFreshPage();
    }
}
