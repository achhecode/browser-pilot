
package com.achhecode.gameorchestrator.service;

import com.achhecode.gameorchestrator.exception.GameOrchestrationException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Service
public class GameOrchestrationService {

    private final RestClient pinpointClient;
    private final RestClient linkedinClient;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> GAME_ENDPOINTS = Map.of("zip", "zip", "wend", "wend", "tango", "tango",
            "queens", "queens", "patches", "patches", "mini-sudoku", "mini-sudoku");

    public GameOrchestrationService(@Qualifier("pinpointClient") RestClient pinpointClient,
            @Qualifier("linkedinClient") RestClient linkedinClient, ObjectMapper objectMapper) {

        this.pinpointClient = pinpointClient;
        this.linkedinClient = linkedinClient;
        this.objectMapper = objectMapper;
    }

    public JsonNode solveAndApply(String game) {

        String normalizedGame = game == null ? "" : game.toLowerCase().trim();

        String gameEndpoint = GAME_ENDPOINTS.get(normalizedGame);

        if (gameEndpoint == null) {
            throw new IllegalArgumentException(
                    "Unsupported game: " + game + ". Supported games: " + GAME_ENDPOINTS.keySet());
        }

        // Step 1: Ask Pinpoint to solve the game.
        JsonNode pinpointResult;

        try {
            pinpointResult = pinpointClient.post().uri("/api/pinpointanswer/{game}/solve", gameEndpoint)
                    .accept(MediaType.APPLICATION_JSON).retrieve().body(JsonNode.class);

        } catch (RestClientResponseException ex) {
            throw new GameOrchestrationException("SOLVING",
                    "Pinpoint service returned HTTP " + ex.getStatusCode().value(), ex);

        } catch (Exception ex) {
            throw new GameOrchestrationException("SOLVING", "Unable to call Pinpoint service", ex);
        }

        if (pinpointResult == null || !pinpointResult.hasNonNull("response")) {

            throw new GameOrchestrationException("SOLVING",
                    "Pinpoint response did not contain a non-null 'response' field");
        }

        // Preserve the complete solution from Pinpoint.
        JsonNode solution = pinpointResult.get("response");

        // Step 2: Wrap the solution in the format LinkedIn expects.
        ObjectNode linkedinRequest = objectMapper.createObjectNode();
        linkedinRequest.set("request", solution.deepCopy());

        // Step 3: Send the solution to LinkedIn Games.
        JsonNode applicationResult;

        try {
            applicationResult = linkedinClient.post().uri("/api/linkedin-games/{game}/solve", gameEndpoint)
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(linkedinRequest)
                    .retrieve().body(JsonNode.class);

        } catch (RestClientResponseException ex) {
            throw new GameOrchestrationException("APPLYING",
                    "Solution was calculated, but LinkedIn Games returned HTTP " + ex.getStatusCode().value(), ex);

        } catch (Exception ex) {
            throw new GameOrchestrationException("APPLYING",
                    "Solution was calculated, but calling LinkedIn Games failed", ex);
        }

        // Step 4: Return the combined result.
        ObjectNode result = objectMapper.createObjectNode();

        result.put("game", gameEndpoint);
        result.put("status", "COMPLETED");
        result.set("solution", solution.deepCopy());

        if (applicationResult != null) {
            result.set("applicationResult", applicationResult);
        } else {
            result.putNull("applicationResult");
        }

        return result;
    }
}
