package digital.moveto.botinok.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.entities.Contact;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ContactDto implements Serializable {

    private UUID id;

    private AccountDto account;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String location;

    private String position;

    private String linkedinUrl;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createdDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate updatedDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate parseDate;

    private String html;

    @Override
    public String toString() {
        return "ContactDto{" +
                "id=" + id +
                ", accountDto=" + account +
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

    public Contact toEntity(){
        return Const.modelMapper.map(this, Contact.class);
    }
}
