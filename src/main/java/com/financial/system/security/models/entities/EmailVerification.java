package com.financial.system.security.models.entities;


import com.financial.system.core.models.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "email_verification")
public class EmailVerification extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "is_deleted")
    private boolean deleted;

    @Column(name = "is_validated")
    private boolean validated;

    @Column(name = "is_canceled")
    private boolean canceled;
}
