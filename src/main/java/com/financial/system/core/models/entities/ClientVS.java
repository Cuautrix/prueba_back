package com.financial.system.core.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client_validation_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientVS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", unique = true)
    private Long clientId;

    @Column(name = "bank_info")
    private boolean bankInfo;

    @Column(name = "address_info")
    private boolean addressInfo;

    @Column(name = "personal_info")
    private boolean personalInfo;

    @Column(name = "documents")
    private boolean documents;
}