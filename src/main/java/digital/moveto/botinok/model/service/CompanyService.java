package digital.moveto.botinok.model.service;

import digital.moveto.botinok.model.entities.Company;
import digital.moveto.botinok.model.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;


    @Transactional
    public Company save(Company company) {
        if (company.getId() == null) {
            company.setId(UUID.randomUUID());
        } else {
            Optional<Company> entityFindInDb = companyRepository.findById(company.getId());
            if (entityFindInDb.isPresent()) {
                company = entityFindInDb.get().updateFrom(company);
            }
        }
        company = companyRepository.save(company);
        return company;
    }

    public List<Company> findAll(){
        return companyRepository.findAll();
    }

    @Transactional
    public Company saveAndFlush(Company company) {
        company = save(company);
        companyRepository.flush();
        return company;
    }

    @Transactional
    public Optional<Company> findByLink(String link) {
        return companyRepository.findByLink(link);
    }

    public Optional<Company> findById(UUID id) {
        return companyRepository.findById(id);
    }
}
