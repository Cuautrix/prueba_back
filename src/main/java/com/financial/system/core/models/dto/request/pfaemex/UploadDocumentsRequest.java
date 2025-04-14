package com.financial.system.core.models.dto.request.pfaemex;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentsRequest {
    @NotEmpty
    private List<UploadDocumentsRequest> documents;
}

