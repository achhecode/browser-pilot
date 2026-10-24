package com.achhecode.playwrightservice.browser;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.Objects;

@Service
public class BrowserNavigationService {

        private final TabManager tabManager;
        private final BrowserUrlValidator urlValidator;

        public BrowserNavigationService(TabManager tabManager, BrowserUrlValidator urlValidator) {
                this.tabManager = Objects.requireNonNull(tabManager, "tabManager");
                this.urlValidator = Objects.requireNonNull(urlValidator, "urlValidator");
        }

        /**
         * Navigates the tab identified by its ID.
         */
        public BrowserTabSnapshot navigate(String tabId, String url) {
                validateTabId(tabId);
                URI target = validateTarget(url);

                tabManager.withTab(tabId, page -> {
                        page.navigate(target.toASCIIString());
                        return null;
                });

                return tabManager.getSnapshot(tabId);
        }

        /**
         * Navigates the tab at the specified zero-based browser-context index.
         */
        public BrowserTabSnapshot navigate(int tabIndex, String url) {
                if (tabIndex < 0) {
                        throw new IllegalArgumentException("Tab index must not be negative.");
                }

                URI target = validateTarget(url);

                tabManager.withTab(tabIndex, page -> {
                        page.navigate(target.toASCIIString());
                        return null;
                });

                BrowserTab tab = tabManager.getTab(tabIndex);
                return tabManager.getSnapshot(tab.getId());
        }

        /**
         * Navigates the first existing tab, creating one if necessary.
         */
        public BrowserTabSnapshot navigate(String url) {
                BrowserTab tab = tabManager.getOrCreateTab();
                return navigate(tab.getId(), url);
        }

        private URI validateTarget(String url) {
                URI target = urlValidator.validate(url);
                urlValidator.rejectNonPublicAddress(target);
                return target;
        }

        private void validateTabId(String tabId) {
                if (tabId == null || tabId.isBlank()) {
                        throw new IllegalArgumentException("Tab ID must not be blank.");
                }
        }
}