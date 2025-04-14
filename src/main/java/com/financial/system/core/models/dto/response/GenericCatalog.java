package com.financial.system.core.models.dto.response;


import com.financial.system.core.models.enums.ClientTypeEnum;
import com.financial.system.core.models.enums.NationalityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericCatalog {
    Long id;
    String description;

    public GenericCatalog(Long id, NationalityEnum nationalityEnum) {
        this.id = id;
        this.description = nationalityEnum.name();
    }

    public GenericCatalog(Long id, ClientTypeEnum clientTypeEnum) {
        this.id = id;
        this.description = clientTypeEnum.name();
    }
}
