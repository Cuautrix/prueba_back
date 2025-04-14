package com.financial.system.core.models.entities;

import com.financial.system.core.models.dao.ClientVSDAO;
import com.financial.system.security.context.FSUserDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "client_entity")
public class Client extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "password_expiration_date")
    private LocalDateTime passwordExpirationDate;

    // MFA Google - Secret code
    @Column(name = "mfa_enabled")
    private boolean mfaEnabled;

    // MFA Google - Secret code
    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "privacy_notice_accepted")
    private boolean privacyNoticeAccepted;

    @Column(name = "terms_and_conditions_accepted")
    private boolean termsAndConditionsAccepted;

    @Column(name = "approved_client")
    private boolean approvedClient;

    @Column(name = "last_session_attempt")
    private LocalDateTime lastSessionAttempt;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "scope")
    private String scope;

    @Column(name = "onboarding_stages")
    private String onboardingStages;

    @Column(name = "failed_attempts")
    private int failedAttempts = 0;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "account_deleted")
    private Boolean accountDeleted;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Column(name = "approved_date")
    private Date approvedDate;

    @JoinColumn(name = "nationality_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Nationality nationality;

    @JoinColumn(name = "client_type_id", referencedColumnName = "id")
    @ManyToOne
    private ClientType clientType;

    @JoinColumn(name = "client_validations_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private ClientValidations clientValidations;

    @JoinColumn(name = "id_client_validation_status")
    @ManyToOne(fetch = FetchType.LAZY)
    private ClientValidationStatus clientValidationStatus;

    @Column(name = "rol")
    private String rol;

    public UserDetails toUserDetails() {
        return new FSUserDetails(this);
    }



}
