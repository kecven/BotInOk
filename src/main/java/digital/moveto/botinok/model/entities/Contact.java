package digital.moveto.botinok.model.entities;

import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.dto.ContactDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@SuperBuilder
@Table(name = "contact", indexes = {@Index(name = "contact_index_linkedin_url", columnList = "linkedin_url")})
public class Contact {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "location")
    private String location;

    @Column(name = "position")
    private String position;

    @Column(name = "linkedin_url", nullable = false, unique = true)
    private String linkedinUrl;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDate createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDate updatedDate;

    @Column(name = "parse_date")
    private LocalDate parseDate;

    @Column(name = "html", length = 10_000_000)
    private String html;

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", account_id=" + account.getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", position='" + position + '\'' +
                ", linkedinUrl='" + linkedinUrl + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", parseDate=" + parseDate +
                ", html='" + html + '\'' +
                '}';
    }

    public Contact updateFrom(Contact entity) {
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

    public ContactDto toDto(){
        return Const.modelMapper.map(this,ContactDto.class);
    }
}
