
package com.achhecode.playwrightservice.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class TabManager {

        private final PlaywrightManager playwrightManager;
        private final BrowserUrlValidator urlValidator;

        /*
         * All access to the registry and Page objects must happen inside
         * PlaywrightManager.executeInContext().
         */
        private final Map<String, BrowserTab> tabs = new ConcurrentHashMap<>();

        public TabManager(PlaywrightManager playwrightManager, BrowserUrlValidator urlValidator) {
                this.playwrightManager = Objects.requireNonNull(playwrightManager, "playwrightManager");
                this.urlValidator = Objects.requireNonNull(urlValidator, "urlValidator");
        }

        /**
         * Finds a tab whose URL exactly matches the target. Opens a new tab if no match
         * exists.
         */
        public BrowserTab findOrOpenTab(String url) {
                URI target = urlValidator.validate(url);
                urlValidator.rejectNonPublicAddress(target);

                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        for (Page page : context.pages()) {
                                if (!page.isClosed() && urlValidator.matchesExactly(page.url(), target)) {
                                        return findByPage(page);
                                }
                        }

                        Page page = context.newPage();
                        BrowserTab tab = registerPage(page);

                        try {
                                page.navigate(target.toASCIIString());
                                return tab;
                        } catch (RuntimeException exception) {
                                removeTab(tab.getId());

                                try {
                                        if (!page.isClosed()) {
                                                page.close();
                                        }
                                } catch (RuntimeException closeException) {
                                        exception.addSuppressed(closeException);
                                }

                                throw exception;
                        }
                });
        }

        /**
         * Returns the first open tab, creating one if necessary.
         */
        public BrowserTab getOrCreateTab() {
                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        List<Page> pages = context.pages();

                        if (!pages.isEmpty()) {
                                return findByPage(pages.getFirst());
                        }

                        return registerPage(context.newPage());
                });
        }

        /**
         * Returns an existing blank tab or creates a new one.
         */
        public BrowserTab getFreshTab() {
                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        for (Page page : context.pages()) {
                                if (isBlankPage(page)) {
                                        return findByPage(page);
                                }
                        }

                        return registerPage(context.newPage());
                });
        }

        /**
         * Returns the last page in browser-context order. This does not necessarily
         * represent the foreground tab.
         */
        public BrowserTab getCurrentOpenTab() {
                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        List<Page> pages = context.pages();

                        if (!pages.isEmpty()) {
                                return findByPage(pages.getLast());
                        }

                        return registerPage(context.newPage());
                });
        }

        /**
         * Creates a new browser tab.
         */
        public BrowserTab createTab() {
                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);
                        return registerPage(context.newPage());
                });
        }

        /**
         * Returns open tabs in browser-context order. Index 0 corresponds to
         * context.pages().get(0).
         */
        public List<BrowserTab> getTabs() {
                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        return context.pages().stream().filter(page -> !page.isClosed()).map(this::findByPage).toList();
                });
        }

        /**
         * Returns a tab by ID.
         */
        public BrowserTab getTab(String tabId) {
                return withTab(tabId, this::findByPage);
        }

        /**
         * Returns a tab by its zero-based index in browser-context order.
         */
        public BrowserTab getTab(int tabIndex) {
                if (tabIndex < 0) {
                        throw new IllegalArgumentException("Tab index must not be negative.");
                }

                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        List<Page> pages = context.pages().stream().filter(page -> !page.isClosed()).toList();

                        if (tabIndex >= pages.size()) {
                                throw new IllegalArgumentException("No tab exists at index " + tabIndex
                                                + ". Available tabs: " + pages.size());
                        }

                        return findByPage(pages.get(tabIndex));
                });
        }

        /**
         * Executes an operation against a tab identified by ID.
         */
        public <T> T withTab(String tabId, Function<Page, T> operation) {
                if (tabId == null || tabId.isBlank()) {
                        throw new IllegalArgumentException("Tab ID must not be blank.");
                }

                Objects.requireNonNull(operation, "operation");

                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        BrowserTab tab = tabs.get(tabId);

                        if (tab == null) {
                                throw new IllegalArgumentException("Tab not found: " + tabId);
                        }

                        Page page = tab.page();

                        if (page.isClosed()) {
                                removeTab(tabId);
                                throw new IllegalStateException("Tab is already closed: " + tabId);
                        }

                        return operation.apply(page);
                });
        }

        /**
         * Executes an operation against a tab by zero-based index.
         */
        public <T> T withTab(int tabIndex, Function<Page, T> operation) {
                if (tabIndex < 0) {
                        throw new IllegalArgumentException("Tab index must not be negative.");
                }

                Objects.requireNonNull(operation, "operation");

                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        List<Page> pages = context.pages().stream().filter(page -> !page.isClosed()).toList();

                        if (tabIndex >= pages.size()) {
                                throw new IllegalArgumentException("No tab exists at index " + tabIndex
                                                + ". Available tabs: " + pages.size());
                        }

                        return operation.apply(pages.get(tabIndex));
                });
        }

        /**
         * Executes an operation against the last open tab, creating one if necessary.
         */
        public <T> T withCurrentOpenTab(Function<Page, T> operation) {
                Objects.requireNonNull(operation, "operation");

                return playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        List<Page> pages = context.pages();

                        Page page = pages.isEmpty() ? context.newPage() : pages.getLast();

                        if (page.isClosed()) {
                                page = context.newPage();
                        }

                        findByPage(page);
                        return operation.apply(page);
                });
        }

        /**
         * Returns tab metadata without exposing the Page object.
         */
        public BrowserTabSnapshot getSnapshot(String tabId) {
                return withTab(tabId, page -> {
                        BrowserTab tab = findByPage(page);

                        return new BrowserTabSnapshot(tab.getId(), page.url(), page.title(), tab.getCreatedAt(),
                                        page.isClosed());
                });
        }

        /**
         * Closes a tab by ID and removes it from the registry.
         */
        public void closeTab(String tabId) {
                if (tabId == null || tabId.isBlank()) {
                        throw new IllegalArgumentException("Tab ID must not be blank.");
                }

                playwrightManager.executeInContext(context -> {
                        syncTabs(context);

                        BrowserTab tab = tabs.get(tabId);

                        if (tab == null) {
                                throw new IllegalArgumentException("Tab not found: " + tabId);
                        }

                        try {
                                Page page = tab.page();

                                if (!page.isClosed()) {
                                        page.close();
                                }
                        } finally {
                                removeTab(tabId);
                        }

                        return null;
                });
        }

        /**
         * Synchronizes the registry with the current context pages. Also discovers
         * pages opened outside this manager.
         */
        private void syncTabs(BrowserContext context) {
                List<Page> pages = context.pages();

                Set<Page> openPages = Collections.newSetFromMap(new IdentityHashMap<>());

                for (Page page : pages) {
                        if (!page.isClosed()) {
                                openPages.add(page);
                        }
                }

                tabs.entrySet().removeIf(entry -> !openPages.contains(entry.getValue().page()));

                for (Page page : pages) {
                        if (!page.isClosed()) {
                                findByPage(page);
                        }
                }
        }

        /**
         * Finds a registered tab by Page object identity.
         */
        private BrowserTab findByPage(Page page) {
                for (BrowserTab tab : tabs.values()) {
                        if (tab.page() == page) {
                                return tab;
                        }
                }

                return registerPage(page);
        }

        private BrowserTab registerPage(Page page) {
                BrowserTab tab = new BrowserTab("tab-" + UUID.randomUUID(), page);

                tabs.put(tab.getId(), tab);
                return tab;
        }

        private void removeTab(String tabId) {
                tabs.remove(tabId);
        }

        private boolean isBlankPage(Page page) {
                if (page.isClosed()) {
                        return false;
                }

                String url = page.url();

                return url == null || url.isBlank() || "about:blank".equalsIgnoreCase(url);
        }

        public List<BrowserTabSnapshot> getSnapshots() {
                System.out.println("[Tabs] Before executeInContext");

                List<BrowserTabSnapshot> result = playwrightManager.executeInContext(context -> {
                        System.out.println("[Tabs] Entered Playwright worker");

                        System.out.println("[Tabs] Before syncTabs");
                        syncTabs(context);
                        System.out.println("[Tabs] After syncTabs");

                        System.out.println("[Tabs] Before context.pages()");
                        List<Page> pages = context.pages();
                        System.out.println("[Tabs] After context.pages(); count = " + pages.size());

                        List<BrowserTabSnapshot> snapshots = new ArrayList<>();

                        for (Page page : pages) {
                                System.out.println("[Tabs] Checking page");

                                System.out.println("[Tabs] Before page.isClosed()");
                                boolean closed = page.isClosed();
                                System.out.println("[Tabs] After page.isClosed(): " + closed);

                                if (closed) {
                                        continue;
                                }

                                System.out.println("[Tabs] Before findByPage()");
                                BrowserTab tab = findByPage(page);
                                System.out.println("[Tabs] After findByPage()");

                                System.out.println("[Tabs] Before page.url()");
                                String url = page.url();
                                System.out.println("[Tabs] After page.url(): " + url);

                                snapshots.add(new BrowserTabSnapshot(tab.getId(), url, "", tab.getCreatedAt(), closed));
                        }

                        System.out.println("[Tabs] Finished collecting snapshots: " + snapshots.size());
                        return List.copyOf(snapshots);
                });

                System.out.println("[Tabs] Returning snapshots");
                return result;
        }
}
