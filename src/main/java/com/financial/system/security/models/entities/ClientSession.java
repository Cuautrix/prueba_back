package com.financial.system.security.models.entities;

import com.financial.system.core.models.entities.Client;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client_session")
public class ClientSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "device")
    private String device;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "agent")
    private String agent;

    @Column(name = "current_session", nullable = false)
    private boolean currentSession = false;

    @Column(name = "expired_session", nullable = false)
    private boolean expired = false;

    @Column(name = "revoked_session", nullable = false)
    private boolean revoked = false;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "extra_information", columnDefinition = "jsonb")
    private String extraInformation;
}
