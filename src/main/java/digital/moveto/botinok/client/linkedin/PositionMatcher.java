package digital.moveto.botinok.client.linkedin;

import java.util.List;
import java.util.Optional;

public interface PositionMatcher {

    Optional<Boolean> isPositionSuitable(List<String> validPositions, String positionToCheck);
}
