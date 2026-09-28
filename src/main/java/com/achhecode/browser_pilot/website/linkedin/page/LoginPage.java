package com.achhecode.browser_pilot.website.linkedin.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Page;

public class LoginPage extends WebsitePage {

    public LoginPage(Page page) {
        super(page);
    }

    public boolean isDisplayed() {
        return page.url().contains("/login");
    }

    public void open() {
        page.navigate("https://www.linkedin.com/login");
    }

    // login()
    // enterUsername()
    // enterPassword()
    // submit()
    // isLoggedIn()
}