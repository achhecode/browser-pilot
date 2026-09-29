package com.achhecode.browser_pilot.website.linkedin.page.games.zip.monitoring;

import java.util.List;

public interface ZipCommandMonitor {

    void start();

    List<String> stop();

    boolean isRunning();

    List<String> getRecordedCommands();
}