package digital.moveto.botinok.client.linkedin;

import digital.moveto.botinok.client.config.GlobalConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultPositionMatcherTest {

    @Test
    void shouldMatchRoleIgnoringSeniorityAndRemoteWords() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.thresholdPositionSuitableScore = 0.75;

        DefaultPositionMatcher matcher = new DefaultPositionMatcher(globalConfig);

        boolean result = matcher.isPositionSuitable(List.of("Java Developer"), "Senior Java Developer Remote")
                .orElse(false);

        assertTrue(result);
    }

    @Test
    void shouldRejectDifferentRole() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.thresholdPositionSuitableScore = 0.75;

        DefaultPositionMatcher matcher = new DefaultPositionMatcher(globalConfig);

        boolean result = matcher.isPositionSuitable(List.of("Recruiter"), "Java Developer")
                .orElse(true);

        assertFalse(result);
    }

    @Test
    void shouldKeepUnicodeTitlesForFallbackMatcher() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.thresholdPositionSuitableScore = 0.75;

        DefaultPositionMatcher matcher = new DefaultPositionMatcher(globalConfig);

        boolean result = matcher.isPositionSuitable(List.of("Разработчик Java"), "Senior Разработчик Java")
                .orElse(false);

        assertTrue(result);
    }
}
