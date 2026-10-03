package com.achhecode.browser_pilot.browser.tab;

import com.achhecode.browser_pilot.browser.BrowserNavigationService;
import com.achhecode.browser_pilot.browser.BrowserTab;
import com.achhecode.browser_pilot.browser.TabManager;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/browser/tabs")
public class BrowserTabController {

    private final TabManager tabManager;

    private final BrowserNavigationService navigationService;

    public BrowserTabController(
            TabManager tabManager,
            BrowserNavigationService navigationService
    ) {
        this.tabManager = tabManager;
        this.navigationService = navigationService;
    }

    /**
     * Create a new browser tab.
     *
     * POST /api/browser/tabs
     */
    @PostMapping
    public ResponseEntity<TabResponse> createTab(
            @RequestBody(required = false)
            CreateTabRequest request
    ) {

        BrowserTab tab =
                tabManager.createTab();

        if (request != null
                && request.url() != null
                && !request.url().isBlank()) {

            tab.getPage().navigate(
                    request.url()
            );
        }

        return ResponseEntity.ok(
                toResponse(tab)
        );
    }

    /**
     * Get all currently open tabs.
     *
     * GET /api/browser/tabs
     */
    @GetMapping
    public ResponseEntity<List<TabResponse>> getTabs() {

        List<TabResponse> response =
                tabManager.getTabs()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Get tab by stable tab ID.
     *
     * GET /api/browser/tabs/{tabId}
     */
    @GetMapping("/{tabId}")
    public ResponseEntity<TabResponse> getTab(
            @PathVariable String tabId
    ) {

        BrowserTab tab =
                tabManager.getTab(tabId);

        return ResponseEntity.ok(
                toResponse(tab)
        );
    }

    /**
     * Get tab by index.
     *
     * Default tab is index 0.
     *
     * GET /api/browser/tabs/index/0
     */
    @GetMapping("/index/{index}")
    public ResponseEntity<TabResponse> getTabByIndex(
            @PathVariable int index
    ) {

        BrowserTab tab =
                tabManager.getTab(index);

        return ResponseEntity.ok(
                toResponse(tab)
        );
    }

    /**
     * Navigate an existing tab.
     *
     * POST /api/browser/tabs/{tabId}/navigate
     */
    @PostMapping("/{tabId}/navigate")
    public ResponseEntity<TabResponse> navigate(
            @PathVariable String tabId,
            @RequestBody NavigateTabRequest request
    ) {

        BrowserTab tab =
                navigationService.navigate(
                        tabId,
                        request.url()
                );

        return ResponseEntity.ok(
                toResponse(tab)
        );
    }

    /**
     * Navigate tab by index.
     *
     * POST /api/browser/tabs/index/{index}/navigate
     */
    @PostMapping("/index/{index}/navigate")
    public ResponseEntity<TabResponse> navigateByIndex(
            @PathVariable int index,
            @RequestBody NavigateTabRequest request
    ) {

        BrowserTab tab =
                navigationService.navigate(
                        index,
                        request.url()
                );

        return ResponseEntity.ok(
                toResponse(tab)
        );
    }

    /**
     * Close a specific tab.
     *
     * DELETE /api/browser/tabs/{tabId}
     */
    @DeleteMapping("/{tabId}")
    public ResponseEntity<Void> closeTab(
            @PathVariable String tabId
    ) {

        tabManager.closeTab(tabId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Close tab by index.
     *
     * DELETE /api/browser/tabs/index/{index}
     */
    @DeleteMapping("/index/{index}")
    public ResponseEntity<Void> closeTabByIndex(
            @PathVariable int index
    ) {

        tabManager.closeTab(index);

        return ResponseEntity.noContent().build();
    }

    private TabResponse toResponse(
            BrowserTab tab
    ) {

        int index =
                tabManager.getTabs()
                        .indexOf(tab);

        return new TabResponse(
                tab.getId(),
                index,
                tab.getUrl(),
                tab.getTitle(),
                tab.isClosed()
        );
    }
}
