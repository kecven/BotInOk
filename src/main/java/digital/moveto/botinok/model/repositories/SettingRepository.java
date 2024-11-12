package digital.moveto.botinok.model.repositories;

import digital.moveto.botinok.model.entities.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByName(String name);
}
