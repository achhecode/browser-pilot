package com.achhecode.browser_pilot.website.instahyre.service;

import com.achhecode.browser_pilot.website.instahyre.InstahyreProperties;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class QuestionnaireAnswerProvider {

    private static final Pattern CV_PATTERN = Pattern.compile("(^|\\s)cv($|\\s)");

    private final Map<String, String> answers = new LinkedHashMap<>();

    private final String resumePath;

    public QuestionnaireAnswerProvider(JsonMapper jsonMapper, ResourceLoader resourceLoader,
            InstahyreProperties properties) {
        this.resumePath = properties.resumePath();

        InstahyreProperties.Questionnaire questionnaire = properties.questionnaire();

        if (questionnaire == null) {
            throw new IllegalStateException("Instahyre questionnaire configuration is missing. "
                    + "Configure website.instahyre.questionnaire.answers-file.");
        } else {
            loadAnswers(jsonMapper, resourceLoader, questionnaire.answersFile());
        }

    }

    private void loadAnswers(JsonMapper jsonMapper, ResourceLoader resourceLoader, String answersFile) {
        if (answersFile == null || answersFile.isBlank()) {
            throw new IllegalStateException("Questionnaire answers file path is not configured.");
        }

        String location = answersFile.trim();

        Resource resource = resolveResource(resourceLoader, location);

        if (!resource.exists()) {
            // throw new IllegalStateException("Questionnaire answers file does not exist: "
            // + location);
            return;
        }

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = jsonMapper.readTree(inputStream);

            if (root == null || !root.path("answers").isArray()) {
                throw new IllegalStateException("Invalid questionnaire JSON. " + "Expected an 'answers' array.");
            }

            for (JsonNode entry : root.path("answers")) {
                String rule = entry.path("rule").asString("").trim();
                String answer = entry.path("answer").asString(null);

                if (rule.isBlank()) {
                    throw new IllegalStateException("Each questionnaire answer must have " + "a non-empty rule.");
                }

                if (answer == null || answer.isBlank()) {
                    throw new IllegalStateException("No answer configured for rule: " + rule);
                }

                if (answers.putIfAbsent(rule, answer) != null) {
                    throw new IllegalStateException("Duplicate questionnaire rule: " + rule);
                }
            }

            if (answers.isEmpty()) {
                throw new IllegalStateException("The questionnaire answers file contains no answers.");
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load questionnaire answers from: " + location, e);
        }
    }

    private Resource resolveResource(ResourceLoader resourceLoader, String location) {
        if (location.startsWith("classpath:") || location.startsWith("file:")) {
            return resourceLoader.getResource(location);
        }

        // Treat an unprefixed path as an external filesystem path.
        Path path = Path.of(location).toAbsolutePath().normalize();

        return new FileSystemResource(path);
    }

    public boolean isResumeQuestion(String question) {
        String normalized = normalize(question);

        return normalized.contains("resume") || normalized.contains("curriculum vitae")
                || CV_PATTERN.matcher(normalized).find();
    }

    public Map<String, String> resolveAnswers(String question) {
        Map<String, String> result = new LinkedHashMap<>();

        for (String part : question.split("\\s*:\\s*")) {
            if (part.isBlank()) {
                continue;
            }

            findAnswer(part).ifPresent(answer -> result.put(part, answer));
        }

        return result;
    }

    public Optional<String> findAnswer(String question) {
        return answers.entrySet().stream().filter(entry -> matches(question, entry.getKey()))
                .map(entry -> entry.getValue()).findFirst();
    }

    public boolean matches(String question, String rule) {
        String normalizedQuestion = normalize(question);
        String normalizedRule = rule.trim();

        // Exclusions take precedence.
        String[] notParts = normalizedRule.split("\\s+NOT\\s+", 2);

        if (notParts.length == 2 && matchesPositive(normalizedQuestion, notParts[1])) {
            return false;
        }

        return matchesPositive(normalizedQuestion, notParts[0]);
    }

    private boolean matchesPositive(String question, String rule) {
        String[] alternatives = rule.split("\\s+OR\\s+");

        for (String alternative : alternatives) {
            String expression = alternative.trim();

            if (expression.startsWith("ONLY ")) {
                String exact = normalize(expression.substring(5));

                if (question.equals(exact)) {
                    return true;
                }

                continue;
            }

            String[] allParts = expression.split("\\s+(?:AND|ALL)\\s+");

            boolean match = true;

            for (String part : allParts) {
                if (!containsPhrase(question, normalize(part))) {
                    match = false;
                    break;
                }
            }

            if (match) {
                return true;
            }
        }

        return false;
    }

    private boolean containsPhrase(String question, String keyword) {
        if (keyword.isBlank()) {
            return false;
        }

        return Pattern.compile("(^|\\s)" + Pattern.quote(keyword) + "($|\\s)").matcher(question).find();
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim().replaceAll("\\s+", " ");
    }

    public String getResumePath() {
        if (resumePath == null || resumePath.isBlank()) {
            throw new IllegalStateException("Resume path is not configured. " + "Set website.instahyre.resume-path "
                    + "in application.properties.");
        }

        return resumePath;
    }
}