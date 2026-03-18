package digital.moveto.botinok.client.linkedin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PositionSuitabilityService {

    private final OllamaPositionMatcher ollamaPositionMatcher;
    private final DefaultPositionMatcher defaultPositionMatcher;

    public PositionSuitabilityService(OllamaPositionMatcher ollamaPositionMatcher, DefaultPositionMatcher defaultPositionMatcher) {
        this.ollamaPositionMatcher = ollamaPositionMatcher;
        this.defaultPositionMatcher = defaultPositionMatcher;
    }

    public boolean isPositionSuitable(List<String> validPositions, String positionToCheck) {
        return ollamaPositionMatcher.isPositionSuitable(validPositions, positionToCheck)
                .orElseGet(() -> {
                    log.info("Falling back to default position matcher for title '{}' and target roles {}", positionToCheck, validPositions);
                    return defaultPositionMatcher.isPositionSuitable(validPositions, positionToCheck).orElse(false);
                });
    }
}
