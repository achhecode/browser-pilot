package com.achhecode.gameorchestrator.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.achhecode.browserpilotdto.enums.LinkedInGame;
import com.achhecode.gameorchestrator.exception.GameOrchestrationException;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Slf4j
@Service
public class GameOrchestrationService {

    private final RestClient pinpointClient;
    private final RestClient linkedinClient;
    private final ObjectMapper objectMapper;
    private final PinpointSolutionStore solutionStore;

    /*
     * LinkedInGame is an enum, so the number of locks is bounded by the number of
     * supported games.
     */
    private final ConcurrentHashMap<LinkedInGame, ReentrantLock> gameLocks = new ConcurrentHashMap<>();

    public GameOrchestrationService(@Qualifier("pinpointClient") RestClient pinpointClient,
            @Qualifier("linkedinClient") RestClient linkedinClient, ObjectMapper objectMapper,
            PinpointSolutionStore solutionStore) {

        this.pinpointClient = pinpointClient;
        this.linkedinClient = linkedinClient;
        this.objectMapper = objectMapper;
        this.solutionStore = solutionStore;
    }

    public JsonNode solveAndApply(LinkedInGame game) {

        if (game == null) {
            throw new IllegalArgumentException("Game must not be null");
        }

        // Capture the logical date exactly once for this request.
        LocalDate logicalDate = solutionStore.getLogicalDate();

        // Resolve the solution from cache or Pinpoint.
        JsonNode solution = getOrSolve(game, logicalDate);

        // Build LinkedIn's expected request payload.
        ObjectNode linkedinRequest = objectMapper.createObjectNode();
        linkedinRequest.set("request", solution.deepCopy());

        // LinkedIn is always called, including on cache hits.
        JsonNode applicationResult = applyToLinkedIn(game, linkedinRequest);

        // Construct the combined response.
        ObjectNode result = objectMapper.createObjectNode();

        result.put("game", game.name());
        result.put("status", "COMPLETED");
        result.set("solution", solution.deepCopy());

        if (applicationResult != null) {
            result.set("applicationResult", applicationResult);
        } else {
            result.putNull("applicationResult");
        }

        return result;
    }

    private JsonNode getOrSolve(LinkedInGame game, LocalDate logicalDate) {

        ReentrantLock lock = gameLocks.computeIfAbsent(game, ignored -> new ReentrantLock());

        lock.lock();

        try {
            /*
             * Check the cache again while holding the lock. Another request may have
             * populated it while this request was waiting.
             */
            Optional<JsonNode> cachedSolution = solutionStore.find(game, logicalDate);

            if (cachedSolution.isPresent()) {
                log.info("Pinpoint cache hit: game={}, date={}", game.keyName(), logicalDate);

                return cachedSolution.get();
            }

            log.info("Pinpoint cache miss: game={}, date={}", game.keyName(), logicalDate);

            JsonNode solution = callPinpoint(game);

            /*
             * Cache failures should not block LinkedIn submission. The solution has already
             * been obtained successfully.
             */
            try {
                solutionStore.save(game, logicalDate, solution);

            } catch (Exception ex) {
                log.warn("Unable to cache solution for game={}, date={}. " + "Continuing with LinkedIn submission.",
                        game.keyName(), logicalDate, ex);
            }

            return solution;

        } finally {
            lock.unlock();
        }
    }

    private JsonNode callPinpoint(LinkedInGame game) {

        try {
            JsonNode pinpointResult = pinpointClient.post().uri("/api/pinpointanswer/{game}/solve", game.keyName())
                    .accept(MediaType.APPLICATION_JSON).retrieve().body(JsonNode.class);

            if (pinpointResult == null || !pinpointResult.hasNonNull("response")) {

                throw new GameOrchestrationException("SOLVING",
                        "Pinpoint response did not contain " + "a non-null 'response' field");
            }

            return pinpointResult.get("response").deepCopy();

        } catch (RestClientResponseException ex) {
            throw new GameOrchestrationException("SOLVING",
                    "Pinpoint service returned HTTP " + ex.getStatusCode().value(), ex);

        } catch (GameOrchestrationException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new GameOrchestrationException("SOLVING", "Unable to call Pinpoint service", ex);
        }
    }

    private JsonNode applyToLinkedIn(LinkedInGame game, ObjectNode linkedinRequest) {

        try {
            return linkedinClient.post().uri("/api/linkedin-games/{game}/solve", game.keyName())
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(linkedinRequest)
                    .retrieve().body(JsonNode.class);

        } catch (RestClientResponseException ex) {
            throw new GameOrchestrationException("APPLYING",
                    "Solution was obtained, but LinkedIn Games " + "returned HTTP " + ex.getStatusCode().value(), ex);

        } catch (Exception ex) {
            throw new GameOrchestrationException("APPLYING",
                    "Solution was obtained, but calling " + "LinkedIn Games failed", ex);
        }
    }
}