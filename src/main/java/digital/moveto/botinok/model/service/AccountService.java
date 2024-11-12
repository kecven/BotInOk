package digital.moveto.botinok.model.service;

import digital.moveto.botinok.model.entities.Account;
import digital.moveto.botinok.model.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static digital.moveto.botinok.model.Const.DEFAULT_LOCATION;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;


    @Transactional
    public Account save(Account account) {
        if (account.getId() == null) {
            account.setId(UUID.randomUUID());
        } else {
            Optional<Account> entityFindInDb = accountRepository.findById(account.getId());
            if (entityFindInDb.isPresent()) {
                account = entityFindInDb.get().updateFrom(account);
            }
        }
        account = accountRepository.save(account);
        return account;
    }

    @Transactional
    public Account findOrCreateAccountWithContacts(String folder) {
        Account account = accountRepository.findByFolder(folder).orElse(null);
        if (account == null) {
            account = new Account();
            account.setFolder(folder);
            account = save(account);
        }
        if (account.getContacts() == null) {
            account.setContacts(new ArrayList<>());
        }
        account.getContacts().size();
        return account;
    }

    @Transactional
    public Optional<Account> findById(UUID id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            return Optional.empty();
        }
        account.getContacts().size();
        return Optional.of(account);
    }

    @Transactional
    public Account addNewAccount() {
        Account account = new Account();
        account.setWorkInShabat(true);
        account.setActive(true);
        account.setActiveSearch(true);
        account.setEndDateLicense(LocalDate.now().plusYears(20));
        account.setLocation(DEFAULT_LOCATION);

        account = save(account);
        account.setFolder(account.getId().toString());
        account = save(account);

        return account;
    }



    @Transactional
    public void deleteByFolder(String folder) {
        accountRepository.deleteByFolder(folder);
    }

    @Transactional
    public List<Account> findAll() {
        return accountRepository.findAll();
    }
    @Transactional
    public List<Account> findAllActive() {
        List<Account> all = accountRepository.findAllByActive(true);
        List<Account> result = new ArrayList<>(all.size());

        for (Account account : all) {
            if (account.getEndDateLicense() == null
                    || !account.getEndDateLicense().isBefore(LocalDate.now())) {
                result.add(account);
            }
        }
        return result;
    }
}
