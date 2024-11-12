package digital.moveto.botinok.model.entities;

import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.dto.MadeContactDto;
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
@Table(name = "made_contact", indexes = {@Index(name = "made_contact_index_linkedin_account_id", columnList = "account_id")})
public class MadeContact {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private LocalDateTime date;

    @Override
    public String toString() {
        return "MadeContact{" +
                "id=" + id +
                ", account_id=" + account.getId() +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }

    public MadeContact updateFrom(MadeContact entity) {
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

    public MadeContactDto toDto(){
        return Const.modelMapper.map(this, MadeContactDto.class);
    }
}
