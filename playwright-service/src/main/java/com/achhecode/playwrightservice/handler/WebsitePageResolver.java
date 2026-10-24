package com.achhecode.playwrightservice.handler;

import com.microsoft.playwright.Page;

public interface WebsitePageResolver {
    Page findPage();
}