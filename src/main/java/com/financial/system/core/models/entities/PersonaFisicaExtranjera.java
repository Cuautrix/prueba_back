package com.financial.system.core.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "entidad_cliente_pfae_ext")
public class PersonaFisicaExtranjera extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente_pfae_ext")
    private Long id;

    @Column(name = "fecha_nacimiento")
    private LocalDate birthDate;

    @Column(name = "telefono")
    private String phone;

    @Column(name = "id_cliente")
    private Long clientId;

    @Column(name = "correo_electronico")
    private String email;


    @Column(name = "id_pais_nacimiento")
    private Integer idCountryOrigin;

    @Column(name = "id_nacionalidad")
    private Integer idNationality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero")
    private Gender gender;

    @Column(name = "nombre")
    private String name;
    @Column(name = "apellido_paterno")
    private String lastName;
    @Column(name = "apellido_materno")
    private String secondLastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ocupacion")
    private Occupation occupation;

    @Column(name = "domicilio")
    private String address;

    @Column(name = "id_entidad_federativa")
    private Long entidadFederativaId;

    @Column(name = "direccion")
    private String corpDirection;

    @Column(name = "rfc")
    private String rfc;

    @Column(name = "curp")
    private String curp;

    @Column(name = "direccion_local")
    private String localAddress;

    @Column(name = "direccion_extranjera")
    private String internationalAddress;
}
