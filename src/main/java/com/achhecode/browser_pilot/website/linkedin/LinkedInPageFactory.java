package com.achhecode.browser_pilot.website.linkedin;

import com.achhecode.browser_pilot.website.linkedin.page.HomePage;
import com.achhecode.browser_pilot.website.linkedin.page.LoginPage;
import com.achhecode.browser_pilot.website.linkedin.page.MyNetworkPage;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

@Component
public class LinkedInPageFactory {

    public HomePage home(Page page) {
        return new HomePage(page);
    }

    public LoginPage login(Page page) {
        return new LoginPage(page);
    }

    public MyNetworkPage myNetwork(Page page) {
        return new MyNetworkPage(page);
    }
}