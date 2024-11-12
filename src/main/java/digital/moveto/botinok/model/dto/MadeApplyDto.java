package digital.moveto.botinok.model.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.entities.MadeApply;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MadeApplyDto implements Serializable {

    private UUID id;

    private AccountDto account;

    private CompanyDto company;

    private String position;

    private String link;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;


    public MadeApply toEntity(){
        return Const.modelMapper.map(this, MadeApply.class);
    }
}
