package digital.moveto.botinok.model.repositories;

import digital.moveto.botinok.model.entities.Account;
import digital.moveto.botinok.model.entities.MadeApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MadeApplyRepository extends JpaRepository<MadeApply, UUID> {
    List<MadeApply> findAllByAccount(Account account);
}
