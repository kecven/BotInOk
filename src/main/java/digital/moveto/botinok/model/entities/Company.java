package digital.moveto.botinok.model.entities;

import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.dto.CompanyDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@SuperBuilder
@Table(name = "company")
public class Company {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "link")
    private String link;

    public CompanyDto toDto(){
        return Const.modelMapper.map(this,CompanyDto.class);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
    public Company updateFrom(Company entity) {
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
}
