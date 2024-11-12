package digital.moveto.botinok.model.repositories;

import digital.moveto.botinok.model.entities.Account;
import digital.moveto.botinok.model.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    Optional<Contact> findByLinkedinUrl(String linkedinUrl);
    List<Contact> findAllByAccount(Account account);
}
