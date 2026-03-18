package digital.moveto.botinok.client.linkedin;

import digital.moveto.botinok.client.config.GlobalConfig;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static digital.moveto.botinok.client.config.ClientConst.SIMILARITY;
import static digital.moveto.botinok.client.config.ClientConst.STOP_WORDS;

@Service
public class DefaultPositionMatcher implements PositionMatcher {

    private final GlobalConfig globalConfig;

    public DefaultPositionMatcher(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public Optional<Boolean> isPositionSuitable(List<String> validPositions, String positionToCheck) {
        if (validPositions == null || validPositions.isEmpty()) {
            return Optional.of(false);
        }

        String normalizedToCheck = normalize(positionToCheck);
        if (normalizedToCheck.isBlank()) {
            return Optional.of(false);
        }

        for (String valid : validPositions) {
            String normalizedValid = normalize(valid);
            double score = SIMILARITY.apply(normalizedValid, normalizedToCheck);
            if (score > globalConfig.thresholdPositionSuitableScore) {
                return Optional.of(true);
            }
        }

        return Optional.of(false);
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        String cleaned = text.toLowerCase()
                .replaceAll("[^\\p{L}\\p{N} ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleaned.isBlank()) {
            return "";
        }

        List<String> words = Arrays.asList(cleaned.split(" "));
        return words.stream()
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.joining(" "));
    }
}
