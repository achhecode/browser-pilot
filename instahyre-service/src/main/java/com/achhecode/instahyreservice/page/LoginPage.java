package com.achhecode.instahyreservice.page;

import com.achhecode.instahyreservice.InstahyreUrls;
import com.achhecode.instahyreservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage extends WebsitePage {

        private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

        public LoginPage(Page page) {
                super(page);
        }

        public void login(String username, String password) {

                Locator emailField = page.locator("#email");
                Locator passwordField = page.locator("#password");
                Locator loginButton = page.locator("button[type='submit'].btn-filled.success");

                // Wait until the login form is visible.
                emailField.waitFor(new Locator.WaitForOptions()
                                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));

                passwordField.waitFor(new Locator.WaitForOptions()
                                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));

                // Fill the fields.
                emailField.fill(username);
                passwordField.fill(password);

                // Verify that both fields contain the expected values.
                boolean usernameFilled = username.equals(emailField.inputValue());

                boolean passwordFilled = password.equals(passwordField.inputValue());

                if (!usernameFilled || !passwordFilled) {
                        log.error("Login aborted: usernameFilled={}, passwordFilled={}", usernameFilled,
                                        passwordFilled);

                        throw new IllegalStateException(
                                        "Login aborted because the credentials were not filled correctly.");
                }

                // Only submit after both fields have been verified.
                loginButton.waitFor(new Locator.WaitForOptions()
                                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));

                if (!loginButton.isEnabled()) {
                        throw new IllegalStateException("Login aborted because the login button is disabled.");
                }

                log.info("Credentials filled successfully. Submitting login form.");
                loginButton.click();
        }

        public boolean isDisplayed() {
                return page.url().contains(InstahyreUrls.LOGIN);
        }
}