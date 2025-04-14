package com.financial.system.core.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDocumentDTO {
    private Long id;
    private Long clientId;
    private String documentType;
    private String fileName;
    private String filePath;
    private String fileExtension;
    private LocalDateTime uploadedAt;
}
