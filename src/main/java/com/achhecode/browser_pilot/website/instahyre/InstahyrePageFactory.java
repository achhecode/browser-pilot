package com.achhecode.browser_pilot.website.instahyre;

import org.springframework.stereotype.Component;

import com.achhecode.browser_pilot.website.instahyre.page.HomePage;
import com.achhecode.browser_pilot.website.instahyre.page.LoginPage;
import com.achhecode.browser_pilot.website.instahyre.page.OpportunitiesPage;
import com.microsoft.playwright.Page;

@Component 
public class InstahyrePageFactory {

    public HomePage home(Page page) {
        return new HomePage(page);
    }

    public LoginPage login(Page page) {
        return new LoginPage(page);
    }

    public OpportunitiesPage opportunities(Page page) {
        return new OpportunitiesPage(page);
    }
}