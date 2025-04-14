package com.financial.system.core.models.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@SuperBuilder
@Table(name = "cat_entidad_federativa")
public class EntidadFederativa extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entidad_federativa")
    private Long id;

    @Column(name = "descripcion")
    private String name;

    @Column(name = "codigo_estado")
    private String code;

    @Column(name = "codigo_num_estado")
    private Long numCode;

}
