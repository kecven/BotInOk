package digital.moveto.botinok.model.repositories;

import digital.moveto.botinok.model.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByFolder(String folder);
    void deleteByFolder(String folder);
    Optional<Account> findById(UUID id);


    List<Account> findAllByActive(Boolean active);

}
