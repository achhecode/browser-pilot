package com.achhecode.browser_pilot.website.linkedin.page.games.zip.monitoring;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZipCommandMonitorService {

    private final ZipCommandMonitor keyboardMonitor;

    public ZipCommandMonitorService(
            ZipCommandMonitor keyboardMonitor
    ) {
        this.keyboardMonitor = keyboardMonitor;
    }

    public void startTracking() {
        keyboardMonitor.start();
    }

    public List<String> stopTracking() {
        return keyboardMonitor.stop();
    }

    public boolean isTracking() {
        return keyboardMonitor.isRunning();
    }

    public List<String> getRecordedCommands() {
        return keyboardMonitor.getRecordedCommands();
    }
}