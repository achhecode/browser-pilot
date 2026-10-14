package com.achhecode.browser_pilot.website.instahyre.page;

import com.achhecode.browser_pilot.website.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage extends WebsitePage {

    public HomePage(Page page) {
        super(page);
    }

    public void clickLogin() {

        Locator login =
            page.locator(
                    "a.login-link[href='/login/']"
            );

        login.click();
    }


    public void clickSignup() {

        Locator singup =
            page.locator(
                    "a.login-link[href='/candidates/register/']"
            );

        singup.click();
    }    
}