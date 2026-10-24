/*
package com.achhecode.playwrightservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.achhecode.playwrightservice.browser.BrowserNavigationService;
import com.achhecode.playwrightservice.browser.BrowserTab;
import com.achhecode.playwrightservice.browser.TabManager;
import com.achhecode.playwrightservice.browser.tab.CreateTabRequest;
import com.achhecode.playwrightservice.browser.tab.NavigateTabRequest;
import com.achhecode.playwrightservice.browser.tab.TabResponse;

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

    @GetMapping
    public ResponseEntity<List<TabResponse>> getTabs() {

        List<TabResponse> response =
                tabManager.getTabs()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

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


    @DeleteMapping("/{tabId}")
    public ResponseEntity<Void> closeTab(
            @PathVariable String tabId
    ) {

        tabManager.closeTab(tabId);

        return ResponseEntity.noContent().build();
    }

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

*/