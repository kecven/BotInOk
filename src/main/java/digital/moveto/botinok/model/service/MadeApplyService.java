package digital.moveto.botinok.model.service;

import digital.moveto.botinok.model.entities.Account;
import digital.moveto.botinok.model.entities.MadeApply;
import digital.moveto.botinok.model.repositories.MadeApplyRepository;
import digital.moveto.botinok.model.utils.BotinokUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MadeApplyService {

    @Autowired
    protected MadeApplyRepository madeApplyRepository;


    @Transactional
    public MadeApply save(MadeApply madeApply) {
        if (madeApply.getId() == null) {
            madeApply.setId(UUID.randomUUID());
        } else {
            Optional<MadeApply> entityFindInDb = madeApplyRepository.findById(madeApply.getId());
            if (entityFindInDb.isPresent()) {
                madeApply = entityFindInDb.get().updateFrom(madeApply);
            }
        }
        madeApply = madeApplyRepository.save(madeApply);
        return madeApply;
    }

    public List<MadeApply> findAll(){
        return madeApplyRepository.findAll();
    }

//    @Transactional
//    public MadeApply saveAndFlush(MadeApply madeApply) {
//        madeApply = save(madeApply);
//        madeApplyRepository.flush();
//        return madeApply;
//    }

    @Transactional
    public List<MadeApply> findAllByAccount(Account account){
        return madeApplyRepository.findAllByAccount(account);
    }

    public int getCountOfTodayForAccount(Account account){
        LocalDate todayLocalDate = LocalDate.now();
        List<MadeApply> allApplyForAccount = findAllByAccount(account);
        final long finalTodayApply = allApplyForAccount.parallelStream()
                .filter(madeApply -> BotinokUtils.equalsDateAndDateTime(todayLocalDate, madeApply.getDate())).count();
        return (int) finalTodayApply;
    }

    public int getCountApplyFor24HoursForAccount(Account account){
        List<MadeApply> allApplyForAccount = findAllByAccount(account);
        return getCountApplyFor24HoursForAccount(allApplyForAccount);
    }

    public int getCountApplyFor24HoursForAccount(List<MadeApply> allApplyForAccount){
        LocalDateTime oneDayBeforeLocalDateTime = LocalDateTime.now().minusDays(1).plusMinutes(30);
        final long finalTodayApply = allApplyForAccount.parallelStream()
                .filter(madeApply -> madeApply.getDate().isAfter(oneDayBeforeLocalDateTime))
                .count();
        return (int) finalTodayApply;
    }
}
