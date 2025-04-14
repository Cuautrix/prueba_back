package com.financial.system.core.models.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@ToString
@Table( name = "client_validations")
public class ClientValidations extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lista_69b", columnDefinition = "text")
    private String lista69b;

    @Column(name = "rfc", columnDefinition = "text")
    private String rfc;

    @Column(name = "curp", columnDefinition = "text")
    private String curp;

    @Column(name = "datos_fiscales", columnDefinition = "text")
    private String datosFiscales;

    @Column(name = "ocr_ine", columnDefinition = "text")
    private String ocrIne;

    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
