package com.financial.system.shared.payload;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FinancialSystemResponse {
    private String message;
    private Object data;
    private Object status;
    private int code;
}
