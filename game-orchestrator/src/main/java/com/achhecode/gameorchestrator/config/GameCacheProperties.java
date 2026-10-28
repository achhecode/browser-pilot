package com.achhecode.gameorchestrator.config;

import java.nio.file.Path;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "game-cache")
public class GameCacheProperties {

    private Path directory = Path.of("./data/pinpoint-solutions");

    private LocalTime dayStart = LocalTime.of(12, 35);

    private String zoneId = "Asia/Kolkata";

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public LocalTime getDayStart() {
        return dayStart;
    }

    public void setDayStart(LocalTime dayStart) {
        this.dayStart = dayStart;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public ZoneId getZone() {
        return ZoneId.of(zoneId);
    }
}