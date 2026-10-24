package com.achhecode.playwrightservice.autoconfigure;

import com.achhecode.playwrightservice.browser.BrowserPageResolver;
import com.achhecode.playwrightservice.browser.BrowserUrlValidator;
import com.achhecode.playwrightservice.browser.PlaywrightManager;
import com.achhecode.playwrightservice.browser.TabManager;
import com.achhecode.playwrightservice.config.BrowserPilotProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(BrowserPilotProperties.class)
@Import({ BrowserUrlValidator.class, PlaywrightManager.class, TabManager.class, BrowserPageResolver.class })
public class PlaywrightServiceAutoConfiguration {
}