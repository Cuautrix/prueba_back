package com.financial.system.core.models.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private String name;

    private String lastNameFather;

    private String lastNameMother;

    private String email;

    private String username;
}
