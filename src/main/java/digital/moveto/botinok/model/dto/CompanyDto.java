package digital.moveto.botinok.model.dto;

import digital.moveto.botinok.model.Const;
import digital.moveto.botinok.model.entities.Company;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class CompanyDto implements Serializable {

    private UUID id;

    private String name;

    private String link;

    public Company toEntity(){
        return Const.modelMapper.map(this, Company.class);
    }
}
