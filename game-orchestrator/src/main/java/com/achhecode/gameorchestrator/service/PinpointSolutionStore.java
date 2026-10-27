package com.achhecode.gameorchestrator.service;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.achhecode.browserpilotdto.enums.LinkedInGame;
import com.achhecode.gameorchestrator.config.GameCacheProperties;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class PinpointSolutionStore {

    private final GameCacheProperties properties;
    private final ObjectMapper objectMapper;

    public PinpointSolutionStore(GameCacheProperties properties, ObjectMapper objectMapper) {

        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * Returns the logical date in the configured timezone. The logical day starts
     * at the configured dayStart time.
     */
    public LocalDate getLogicalDate() {
        ZonedDateTime now = ZonedDateTime.now(properties.getZone());

        if (now.toLocalTime().isBefore(properties.getDayStart())) {
            return now.toLocalDate().minusDays(1);
        }

        return now.toLocalDate();
    }

    private Path getFilePath(LinkedInGame game, LocalDate logicalDate) {

        if (game == null || logicalDate == null) {
            throw new IllegalArgumentException("Game and logical date must not be null");
        }

        String gameName = game.keyName();

        if (gameName == null || !gameName.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid game key: " + gameName);
        }

        Path directory = properties.getDirectory();

        if (directory == null) {
            throw new IllegalStateException("game-cache.directory is not configured");
        }

        return directory.resolve(logicalDate.toString()).resolve(gameName + ".json");
    }

    /**
     * Returns empty when no cache file exists. Invalid or unreadable cache files
     * are reported as errors.
     */
    public Optional<JsonNode> find(LinkedInGame game, LocalDate logicalDate) {

        Path file = getFilePath(game, logicalDate);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("Cache path is not a regular file: " + file);
        }

        try {
            JsonNode solution = objectMapper.readTree(file);

            if (solution == null || solution.isNull()) {
                throw new IllegalStateException("Cached solution is empty: " + file);
            }

            return Optional.of(solution);

        } catch (Exception ex) {
            if (ex instanceof IllegalStateException illegalStateException) {
                throw illegalStateException;
            }

            throw new IllegalStateException("Unable to read cached solution: " + file, ex);
        }
    }

    /**
     * Writes the solution to a temporary file, then replaces the target.
     */
    public void save(LinkedInGame game, LocalDate logicalDate, JsonNode solution) {

        if (solution == null || solution.isNull()) {
            throw new IllegalArgumentException("Cannot cache a null solution");
        }

        Path file = getFilePath(game, logicalDate);
        Path tempFile = null;

        try {
            Files.createDirectories(file.getParent());

            tempFile = Files.createTempFile(file.getParent(), game.keyName() + "-", ".tmp");

            objectMapper.writeValue(tempFile.toFile(), solution);

            try {
                Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Pinpoint solution cached: game={}, date={}, path={}", game.keyName(), logicalDate,
                    file.toAbsolutePath());

        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save cached solution: " + file, ex);

        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ex) {
                    log.warn("Could not clean up temporary cache file: {}", tempFile, ex);
                }
            }
        }
    }
}