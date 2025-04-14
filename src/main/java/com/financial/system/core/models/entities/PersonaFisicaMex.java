package com.financial.system.core.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PersonaFisicaMex extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente_pfae_mex")
    private Long id;

    @Column(name = "fecha_nacimiento")
    private LocalDate birthDate;

    @Column(name = "telefono")
    private String phone;

    @Column(name = "id_cliente")
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero")
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ocupacion")
    private Occupation occupation;

    @Column(name = "id_pais")
    private Long paisOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entidad_federativa")
    private EntidadFederativa entidadFederativa;

    @Column(name = "nombre")
    private String name;
    @Column(name = "apellido_paterno")
    private String lastName;
    @Column(name = "apellido_materno")
    private String secondLastName;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "colonia")
    private String colonia;

    @Column(name = "municipio")
    private String municipio;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "calle")
    private String calle;

    @Column(name = "numero_exterior")
    private String numeroExterior;

    @Column(name = "numero_interior")
    private String numeroInterior;

    @Column(name = "curp")
    private String curp;

    @Column(name = "rfc")
    private String rfc;

    @Column(name = "id_suburb")
    private String idsuburb;
}
