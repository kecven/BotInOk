package digital.moveto.botinok.model.entities;


import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.dto.AccountDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "folder")
    private String folder;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "short_comment")
    private String shortComment;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "active_search")
    private Boolean activeSearch;

    @Column(name = "position")
    private String position;

    @Column(name = "count_daily_apply", columnDefinition = "int default 15", nullable = false)
    private Integer countDailyApply;


    @Column(name = "count_daily_connect", columnDefinition = "int default 5", nullable = false)
    private Integer countDailyConnect;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "end_date_license")
    private LocalDate endDateLicense;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Contact> contacts;

    @Column(name = "work_in_shabat")
    private Boolean workInShabat;

    @Column(name = "remote_work", nullable = true, columnDefinition = "boolean default false")
    private Boolean remoteWork;

    public String getShortComment(){
        return shortComment == null ? "" : shortComment;
    }

    public String getAllLocationKeys(){
        return location;
    }

    public String getLocation(){
        return getRandomLocation();
    }

    public String[] getAllLocations(){
        return location.split(",");
    }

    // give new location every minute
    public String getRandomLocation(){
        String[] locations = getAllLocations();
        long currentTimeMinutes = System.currentTimeMillis() / 60_000;

        int index = (int) (currentTimeMinutes % locations.length);
        return locations[index].trim();
    }

    public String getFullName() {
        if (Strings.isNotBlank(firstName) && Strings.isNotBlank(lastName)) {
            return firstName + " " + lastName;
        }

        if (Strings.isNotBlank(firstName)) {
            return lastName;
        }

        if (Strings.isNotBlank(lastName)) {
            return firstName;
        }

        return "Default";
    }

    public String getShortName() {
        if (Strings.isNotBlank(firstName) && Strings.isNotBlank(lastName)) {
            return firstName.charAt(0) + "" + lastName.charAt(0);
        }

        if (Strings.isNotBlank(firstName)) {
            return lastName;
        }

        if (Strings.isNotBlank(lastName)) {
            return firstName;
        }

        return "Default";
    }

    public AccountDto toDto(){
        return Const.modelMapper.map(this, AccountDto.class);
    }

    public Account updateFrom(Account entity) {
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

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", folder='" + folder + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", activeSearch=" + activeSearch +
                ", position='" + position + '\'' +
                ", countDailyApply=" + countDailyApply +
                ", countDailyConnect=" + countDailyConnect +
                ", location=" + location +
                ", endDateLicense=" + endDateLicense +
                ", workInShabat=" + workInShabat +
                '}';
    }
}
