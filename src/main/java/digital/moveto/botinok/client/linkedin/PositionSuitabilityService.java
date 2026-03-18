package digital.moveto.botinok.client.linkedin;

import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseGet(() -> defaultPositionMatcher.isPositionSuitable(validPositions, positionToCheck).orElse(false));
    }
}
