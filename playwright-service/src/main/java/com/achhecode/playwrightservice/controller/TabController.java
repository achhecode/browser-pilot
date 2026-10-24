
package com.achhecode.playwrightservice.controller;

import com.achhecode.playwrightservice.browser.BrowserTab;
import com.achhecode.playwrightservice.browser.TabManager;
import com.achhecode.playwrightservice.browser.TabResponse;
import com.achhecode.playwrightservice.browser.dto.CreateTabRequest;
import com.achhecode.playwrightservice.browser.dto.NavigateTabRequest;
import com.microsoft.playwright.Page;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tabs")
public class TabController {

    private final TabManager tabManager;

    public TabController(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    @PostMapping
    public ResponseEntity<TabResponse> createTab(@RequestBody(required = false) CreateTabRequest request) {

        if (request != null && request.url() != null && !request.url().isBlank()) {
            validateUrl(request.url());
        }

        BrowserTab tab = tabManager.createTab();

        TabResponse response = tabManager.withTab(tab.getId(), page -> {
            if (request != null && request.url() != null && !request.url().isBlank()) {
                page.navigate(request.url());
            }

            return toResponse(tab.getId(), page);
        });

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TabResponse>> getTabs() {
        List<TabResponse> response = tabManager.getSnapshots().stream().map(snapshot -> new TabResponse(snapshot.id(),
                indexOf(snapshot.id()), snapshot.url(), snapshot.title(), snapshot.createdAt(), snapshot.closed()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tabId}")
    public ResponseEntity<TabResponse> getTab(@PathVariable String tabId) {

        TabResponse response = tabManager.getSnapshots().stream().filter(snapshot -> snapshot.id().equals(tabId))
                .findFirst()
                .map(snapshot -> new TabResponse(snapshot.id(), indexOf(snapshot.id()), snapshot.url(),
                        snapshot.title(), snapshot.createdAt(), snapshot.closed()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tab not found: " + tabId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/index/{index}")
    public ResponseEntity<TabResponse> getTabByIndex(@PathVariable int index) {

        String tabId = getTabIdByIndex(index);
        return getTab(tabId);
    }

    @PostMapping("/{tabId}/navigate")
    public ResponseEntity<TabResponse> navigate(@PathVariable String tabId,
            @Valid @RequestBody NavigateTabRequest request) {

        validateUrl(request.url());

        TabResponse response = tabManager.withTab(tabId, page -> {
            page.navigate(request.url());
            return toResponse(tabId, page);
        });

        return ResponseEntity.ok(response);
    }

    @PostMapping("/index/{index}/navigate")
    public ResponseEntity<TabResponse> navigateByIndex(@PathVariable int index,
            @Valid @RequestBody NavigateTabRequest request) {

        return navigate(getTabIdByIndex(index), request);
    }

    @PostMapping("/{tabId}/reload")
    public ResponseEntity<TabResponse> reload(@PathVariable String tabId) {

        TabResponse response = tabManager.withTab(tabId, page -> {
            page.reload();
            return toResponse(tabId, page);
        });

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tabId}/back")
    public ResponseEntity<TabResponse> goBack(@PathVariable String tabId) {

        TabResponse response = tabManager.withTab(tabId, page -> {
            page.goBack();
            return toResponse(tabId, page);
        });

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tabId}/forward")
    public ResponseEntity<TabResponse> goForward(@PathVariable String tabId) {

        TabResponse response = tabManager.withTab(tabId, page -> {
            page.goForward();
            return toResponse(tabId, page);
        });

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tabId}")
    public ResponseEntity<Void> closeTab(@PathVariable String tabId) {

        // Ensure the tab exists before attempting to close it.
        getTabIdById(tabId);
        tabManager.closeTab(tabId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/index/{index}")
    public ResponseEntity<Void> closeTabByIndex(@PathVariable int index) {

        tabManager.closeTab(getTabIdByIndex(index));

        return ResponseEntity.noContent().build();
    }

    private TabResponse toResponse(String tabId, Page page) {
        return tabManager.getSnapshots().stream().filter(snapshot -> snapshot.id().equals(tabId)).findFirst()
                .map(snapshot -> new TabResponse(tabId, indexOf(tabId), page.url(), page.title(), snapshot.createdAt(),
                        page.isClosed()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tab not found: " + tabId));
    }

    private String getTabIdByIndex(int index) {
        var snapshots = tabManager.getSnapshots();

        if (index < 0 || index >= snapshots.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tab exists at index " + index);
        }

        return snapshots.get(index).id();
    }

    private String getTabIdById(String tabId) {
        return tabManager.getSnapshots().stream().filter(snapshot -> snapshot.id().equals(tabId))
                .map(snapshot -> snapshot.id()).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tab not found: " + tabId));
    }

    private int indexOf(String tabId) {
        var snapshots = tabManager.getSnapshots();

        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshots.get(i).id().equals(tabId)) {
                return i;
            }
        }

        return -1;
    }

    private void validateUrl(String url) {
        try {
            URI uri = URI.create(url);

            if (uri.getHost() == null || uri.getScheme() == null
                    || !(uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("URL must be an absolute HTTP or HTTPS URL");
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL: " + exception.getMessage());
        }
    }
}
