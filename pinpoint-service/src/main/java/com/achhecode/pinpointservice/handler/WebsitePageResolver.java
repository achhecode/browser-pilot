package com.achhecode.pinpointservice.handler;

import com.microsoft.playwright.Page;

public interface WebsitePageResolver {
    Page findPage();
}