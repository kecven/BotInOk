package digital.moveto.botinok.client.linkedin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import digital.moveto.botinok.client.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OllamaPositionMatcher implements PositionMatcher {

    private static final Pattern KEEP_ALIVE_PATTERN = Pattern.compile("^(-?\\d+)(ms|s|m|h)?$");

    private final GlobalConfig globalConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private Instant lastHealthcheckAt;
    private boolean lastHealthcheckResult;
    private Boolean lastModelAvailableResult;
    private boolean missingConfigLogged;
    private Instant modelWarmUntil;

    @Autowired
    public OllamaPositionMatcher(GlobalConfig globalConfig) {
        this(globalConfig, HttpClient.newBuilder()
                .connectTimeout(timeoutDuration(globalConfig.ollamaConnectTimeoutMs))
                .build(), new ObjectMapper());
    }

    OllamaPositionMatcher(GlobalConfig globalConfig, HttpClient httpClient, ObjectMapper objectMapper) {
        this.globalConfig = globalConfig;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Boolean> isPositionSuitable(List<String> validPositions, String positionToCheck) {
        if (!globalConfig.ollamaEnabled) {
            return Optional.empty();
        }

        if (Strings.isBlank(globalConfig.ollamaModel)) {
            if (!missingConfigLogged) {
                missingConfigLogged = true;
                log.warn("Ollama is enabled, but model is empty. Standard position matcher will be used.");
            }
            return Optional.empty();
        }

        if (Strings.isBlank(positionToCheck) || validPositions == null || validPositions.isEmpty()) {
            return Optional.empty();
        }

        if (globalConfig.ollamaHealthcheckEnabled && !isOllamaAvailable()) {
            return Optional.empty();
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(buildUri("/api/generate"))
                    .header("Content-Type", "application/json")
                    .timeout(resolveGenerateTimeout())
                    .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(validPositions, positionToCheck)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("Ollama returned status {} for position suitability check. Falling back to default matcher.", response.statusCode());
                markOllamaUnavailable();
                return Optional.empty();
            }

            OllamaGenerateResponse generateResponse = objectMapper.readValue(response.body(), OllamaGenerateResponse.class);
            Boolean decision = parseDecision(generateResponse.response());
            if (decision == null) {
                log.warn("Ollama returned unexpected payload '{}'. Falling back to default matcher.", generateResponse.response());
                return Optional.empty();
            }

            markModelWarm();
            return Optional.of(decision);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.warn("Failed to get response from Ollama. Falling back to default matcher.", e);
            markOllamaUnavailable();
            return Optional.empty();
        }
    }

    private synchronized boolean isOllamaAvailable() {
        long cacheMs = Math.max(0, globalConfig.ollamaHealthcheckCacheMs);
        if (lastHealthcheckAt != null && cacheMs > 0 && Instant.now().isBefore(lastHealthcheckAt.plusMillis(cacheMs))) {
            return lastHealthcheckResult && (!globalConfig.ollamaCheckModelAvailability || Boolean.TRUE.equals(lastModelAvailableResult));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(buildUri("/api/tags"))
                    .timeout(timeoutDuration(globalConfig.ollamaRequestTimeoutMs))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            lastHealthcheckResult = response.statusCode() >= 200 && response.statusCode() < 300;
            if (!lastHealthcheckResult) {
                log.warn("Ollama healthcheck failed with status {}. Standard position matcher will be used.", response.statusCode());
                lastModelAvailableResult = false;
            } else if (globalConfig.ollamaCheckModelAvailability) {
                lastModelAvailableResult = isModelAvailable(response.body());
                if (!lastModelAvailableResult) {
                    log.warn("Ollama model '{}' is not available on server {}:{}. Standard position matcher will be used.",
                            globalConfig.ollamaModel, globalConfig.ollamaHost, globalConfig.ollamaPort);
                }
            } else {
                lastModelAvailableResult = true;
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            lastHealthcheckResult = false;
            lastModelAvailableResult = false;
            log.warn("Ollama healthcheck failed. Standard position matcher will be used.", e);
        }

        lastHealthcheckAt = Instant.now();
        return lastHealthcheckResult && (!globalConfig.ollamaCheckModelAvailability || Boolean.TRUE.equals(lastModelAvailableResult));
    }

    private synchronized void markOllamaUnavailable() {
        lastHealthcheckResult = false;
        lastModelAvailableResult = false;
        lastHealthcheckAt = Instant.now();
    }

    private URI buildUri(String path) {
        return URI.create("http://" + globalConfig.ollamaHost + ":" + globalConfig.ollamaPort + path);
    }

    private static Duration timeoutDuration(long timeoutMs) {
        return Duration.ofMillis(Math.max(1, timeoutMs));
    }

    private Duration resolveGenerateTimeout() {
        if (modelWarmUntil != null && Instant.now().isBefore(modelWarmUntil)) {
            return timeoutDuration(globalConfig.ollamaRequestTimeoutMs);
        }
        return timeoutDuration(Math.max(globalConfig.ollamaRequestTimeoutMs, globalConfig.ollamaColdStartTimeoutMs));
    }

    private synchronized void markModelWarm() {
        modelWarmUntil = parseKeepAlive(globalConfig.ollamaKeepAlive)
                .map(duration -> Instant.now().plus(duration))
                .orElse(null);
    }

    private Optional<Duration> parseKeepAlive(String keepAlive) {
        if (Strings.isBlank(keepAlive)) {
            return Optional.empty();
        }

        String normalized = keepAlive.trim().toLowerCase();
        Matcher matcher = KEEP_ALIVE_PATTERN.matcher(normalized);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        long amount = Long.parseLong(matcher.group(1));
        if (amount < 0) {
            return Optional.of(Duration.of(3650, ChronoUnit.DAYS));
        }
        if (amount == 0) {
            return Optional.empty();
        }

        String unit = matcher.group(2);
        if (unit == null || unit.isBlank()) {
            return Optional.of(Duration.ofSeconds(amount));
        }

        return switch (unit) {
            case "ms" -> Optional.of(Duration.ofMillis(amount));
            case "s" -> Optional.of(Duration.ofSeconds(amount));
            case "m" -> Optional.of(Duration.ofMinutes(amount));
            case "h" -> Optional.of(Duration.ofHours(amount));
            default -> Optional.empty();
        };
    }

    private boolean isModelAvailable(String responseBody) throws JsonProcessingException {
        OllamaTagsResponse tagsResponse = objectMapper.readValue(responseBody, OllamaTagsResponse.class);
        if (tagsResponse.models() == null || tagsResponse.models().isEmpty()) {
            return false;
        }

        String configuredModel = globalConfig.ollamaModel.trim();
        return tagsResponse.models().stream().anyMatch(model -> modelMatches(configuredModel, model.name(), model.model()));
    }

    private boolean modelMatches(String configuredModel, String name, String model) {
        String normalizedName = normalizeValue(name);
        String normalizedModel = normalizeValue(model);

        if (configuredModel.equalsIgnoreCase(normalizedName) || configuredModel.equalsIgnoreCase(normalizedModel)) {
            return true;
        }

        if (!configuredModel.contains(":")) {
            return (configuredModel + ":latest").equalsIgnoreCase(normalizedName)
                    || (configuredModel + ":latest").equalsIgnoreCase(normalizedModel);
        }

        return false;
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private String buildRequestBody(List<String> validPositions, String positionToCheck) throws JsonProcessingException {
        String prompt = """
                Decide whether the job title matches at least one candidate target role.
                Consider close synonyms as a match.
                Ignore seniority modifiers like senior, junior, lead, principal, staff.
                Ignore work arrangement words like remote, onsite, hybrid.
                Return JSON matching this schema: {"suitable": true|false}

                Candidate target roles: %s
                Job title to evaluate: %s
                """.formatted(String.join(", ", validPositions), positionToCheck);

        OllamaGenerateRequest request = new OllamaGenerateRequest(
                globalConfig.ollamaModel,
                prompt,
                buildFormat(),
                false,
                false,
                Strings.isBlank(globalConfig.ollamaKeepAlive) ? null : globalConfig.ollamaKeepAlive,
                new OllamaOptions(0.0)
        );
        return objectMapper.writeValueAsString(request);
    }

    private Boolean parseDecision(String rawResponse) {
        if (Strings.isBlank(rawResponse)) {
            return null;
        }

        String normalized = rawResponse.trim().toLowerCase();
        if (normalized.contains("\"suitable\":true") || normalized.contains("\"suitable\" : true")) {
            return true;
        }
        if (normalized.contains("\"suitable\":false") || normalized.contains("\"suitable\" : false")) {
            return false;
        }
        if (normalized.startsWith("yes") || normalized.startsWith("true")) {
            return true;
        }
        if (normalized.startsWith("no") || normalized.startsWith("false")) {
            return false;
        }
        return null;
    }

    private OllamaFormat buildFormat() {
        return new OllamaFormat(
                "object",
                new OllamaFormatProperties(new OllamaBooleanProperty("boolean")),
                List.of("suitable")
        );
    }

    private record OllamaGenerateRequest(
            String model,
            String prompt,
            OllamaFormat format,
            boolean stream,
            boolean think,
            String keep_alive,
            OllamaOptions options
    ) {
    }

    private record OllamaOptions(double temperature) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaGenerateResponse(String response, String thinking, Long load_duration) {
    }

    private record OllamaFormat(String type, OllamaFormatProperties properties, List<String> required) {
    }

    private record OllamaFormatProperties(OllamaBooleanProperty suitable) {
    }

    private record OllamaBooleanProperty(String type) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaTagsResponse(List<OllamaTagModel> models) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaTagModel(String name, String model) {
    }
}
