package com.financial.system.core.models.dto.request;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clabe;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "client_id")
    private Long clientId;

}
