package digital.moveto.botinok.model.entities;


import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.dto.MadeApplyDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@SuperBuilder
@Table(name = "made_apply", indexes = {@Index(name = "made_apply_index_linkedin_account_id", columnList = "account_id")})
public class MadeApply {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "position")
    private String position;

    @Column(name = "link")
    private String link;

    @Column(name = "date")
    private LocalDateTime date;

    @Override
    public String toString() {
        return "MadeApply{" +
                "id=" + id +
                ", account_id=" + account.getId() +
                ", company_id=" + company.getId() +
                ", position='" + position + '\'' +
                ", link='" + link + '\'' +
                ", date=" + date +
                '}';
    }

    public MadeApply updateFrom(MadeApply entity) {
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if ( ! field.getName().equalsIgnoreCase("id")) {
                    field.set(this, value);
                }
            } catch (IllegalAccessException e) {
                // handle the exception
            }
        }
        return this;
    }
    public MadeApplyDto toDto(){
        return Const.modelMapper.map(this,MadeApplyDto.class);
    }
}
