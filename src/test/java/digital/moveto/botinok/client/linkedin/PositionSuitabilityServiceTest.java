package digital.moveto.botinok.client.linkedin;

import com.fasterxml.jackson.databind.ObjectMapper;
import digital.moveto.botinok.client.config.GlobalConfig;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PositionSuitabilityServiceTest {

    @Test
    void shouldUseOllamaDecisionWhenAvailable() {
        GlobalConfig globalConfig = createOllamaConfig();
        RecordingHttpClient httpClient = new RecordingHttpClient()
                .respond("/api/tags", 200, "{\"models\":[{\"name\":\"llama3.1\",\"model\":\"llama3.1\"}]}")
                .respond("/api/generate", 200, "{\"response\":\"{\\\"suitable\\\":true}\"}");

        OllamaPositionMatcher ollamaMatcher = new OllamaPositionMatcher(globalConfig, httpClient, new ObjectMapper());
        DefaultPositionMatcher defaultMatcher = new DefaultPositionMatcher(globalConfig);
        PositionSuitabilityService service = new PositionSuitabilityService(ollamaMatcher, defaultMatcher);

        boolean result = service.isPositionSuitable(List.of("Recruiter"), "Java Developer");

        assertTrue(result);
        assertTrue(httpClient.findFirst("/api/generate")
                .flatMap(HttpRequest::timeout)
                .orElseThrow()
                .toMillis() >= globalConfig.ollamaColdStartTimeoutMs);
    }

    @Test
    void shouldFallbackToDefaultMatcherWhenModelIsMissing() {
        GlobalConfig globalConfig = createOllamaConfig();
        RecordingHttpClient httpClient = new RecordingHttpClient()
                .respond("/api/tags", 200, "{\"models\":[{\"name\":\"qwen3:4b\",\"model\":\"qwen3:4b\"}]}");

        OllamaPositionMatcher ollamaMatcher = new OllamaPositionMatcher(globalConfig, httpClient, new ObjectMapper());
        DefaultPositionMatcher defaultMatcher = new DefaultPositionMatcher(globalConfig);
        PositionSuitabilityService service = new PositionSuitabilityService(ollamaMatcher, defaultMatcher);

        boolean result = service.isPositionSuitable(List.of("Java Developer"), "Senior Java Developer");

        assertTrue(result);
        assertTrue(httpClient.findAll("/api/generate").isEmpty());
    }

    @Test
    void shouldFallbackToDefaultMatcherWhenOllamaReturnsUnexpectedAnswer() {
        GlobalConfig globalConfig = createOllamaConfig();
        globalConfig.ollamaHealthcheckEnabled = false;
        RecordingHttpClient httpClient = new RecordingHttpClient()
                .respond("/api/generate", 200, "{\"response\":\"{\\\"unexpected\\\":true}\"}");

        OllamaPositionMatcher ollamaMatcher = new OllamaPositionMatcher(globalConfig, httpClient, new ObjectMapper());
        DefaultPositionMatcher defaultMatcher = new DefaultPositionMatcher(globalConfig);
        PositionSuitabilityService service = new PositionSuitabilityService(ollamaMatcher, defaultMatcher);

        boolean result = service.isPositionSuitable(List.of("Recruiter"), "Java Developer");

        assertFalse(result);
    }

    @Test
    void shouldParseStructuredJsonWithWhitespace() {
        GlobalConfig globalConfig = createOllamaConfig();
        globalConfig.ollamaHealthcheckEnabled = false;
        RecordingHttpClient httpClient = new RecordingHttpClient()
                .respond("/api/generate", 200, "{\"response\":\"{\\n  \\\"suitable\\\": false\\n}\"}");

        OllamaPositionMatcher ollamaMatcher = new OllamaPositionMatcher(globalConfig, httpClient, new ObjectMapper());

        boolean result = ollamaMatcher.isPositionSuitable(List.of("Java Developer"), "Principal Engineer, AI/ML")
                .orElse(true);

        assertFalse(result);
    }

    @Test
    void shouldUseRegularTimeoutWhileModelIsWarm() {
        GlobalConfig globalConfig = createOllamaConfig();
        globalConfig.ollamaHealthcheckCacheMs = 60_000;
        RecordingHttpClient httpClient = new RecordingHttpClient()
                .respond("/api/tags", 200, "{\"models\":[{\"name\":\"llama3.1\",\"model\":\"llama3.1\"}]}")
                .respond("/api/generate", 200, "{\"response\":\"{\\\"suitable\\\":true}\"}")
                .respond("/api/generate", 200, "{\"response\":\"{\\\"suitable\\\":true}\"}");

        OllamaPositionMatcher matcher = new OllamaPositionMatcher(globalConfig, httpClient, new ObjectMapper());

        assertTrue(matcher.isPositionSuitable(List.of("Java Developer"), "Java Developer").orElse(false));
        assertTrue(matcher.isPositionSuitable(List.of("Java Developer"), "Java Developer").orElse(false));

        List<HttpRequest> generateRequests = httpClient.findAll("/api/generate");
        assertEquals(globalConfig.ollamaColdStartTimeoutMs, generateRequests.get(0).timeout().orElseThrow().toMillis());
        assertEquals(globalConfig.ollamaRequestTimeoutMs, generateRequests.get(1).timeout().orElseThrow().toMillis());
    }

    private GlobalConfig createOllamaConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.thresholdPositionSuitableScore = 0.75;
        globalConfig.ollamaEnabled = true;
        globalConfig.ollamaHost = "127.0.0.1";
        globalConfig.ollamaPort = 11434;
        globalConfig.ollamaModel = "llama3.1";
        globalConfig.ollamaHealthcheckEnabled = true;
        globalConfig.ollamaCheckModelAvailability = true;
        globalConfig.ollamaHealthcheckCacheMs = 0;
        globalConfig.ollamaConnectTimeoutMs = 500;
        globalConfig.ollamaRequestTimeoutMs = 500;
        globalConfig.ollamaColdStartTimeoutMs = 10_000;
        globalConfig.ollamaKeepAlive = "15m";
        return globalConfig;
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final Map<String, List<FakeResponseConfig>> responses = new HashMap<>();
        private final List<HttpRequest> requests = new ArrayList<>();

        private RecordingHttpClient respond(String path, int statusCode, String body) {
            responses.computeIfAbsent(path, ignored -> new ArrayList<>()).add(new FakeResponseConfig(statusCode, body));
            return this;
        }

        private Optional<HttpRequest> findFirst(String path) {
            return requests.stream().filter(request -> request.uri().getPath().equals(path)).findFirst();
        }

        private List<HttpRequest> findAll(String path) {
            return requests.stream().filter(request -> request.uri().getPath().equals(path)).toList();
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.empty();
        }

        @Override
        public Redirect followRedirects() {
            return Redirect.NEVER;
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public SSLContext sslContext() {
            return null;
        }

        @Override
        public SSLParameters sslParameters() {
            return new SSLParameters();
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public Version version() {
            return Version.HTTP_1_1;
        }

        @Override
        public Optional<Executor> executor() {
            return Optional.empty();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            requests.add(request);
            List<FakeResponseConfig> configs = responses.get(request.uri().getPath());
            if (configs == null || configs.isEmpty()) {
                throw new IllegalStateException("No fake response configured for path " + request.uri().getPath());
            }
            FakeResponseConfig config = configs.remove(0);
            return new FakeHttpResponse<>(request, config.statusCode(), (T) config.body());
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            return CompletableFuture.completedFuture(send(request, responseBodyHandler));
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            return CompletableFuture.completedFuture(send(request, responseBodyHandler));
        }
    }

    private record FakeResponseConfig(int statusCode, String body) {
    }

    private record FakeHttpResponse<T>(HttpRequest request, int statusCode, T body) implements HttpResponse<T> {

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(Map.of(), (name, value) -> true);
        }

        @Override
        public T body() {
            return body;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return request.uri();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
