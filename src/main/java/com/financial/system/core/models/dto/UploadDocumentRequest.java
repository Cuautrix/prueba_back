package com.financial.system.core.models.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentRequest {

    @NotBlank
    private String filename;

    @NotBlank
    private String base64FileContent;

    @NotBlank
    private String key; // Ej: "INE_FRENTE"

    @NotNull
    private Integer fileType;

    @Column(name = "active")
    private Boolean active;

}
