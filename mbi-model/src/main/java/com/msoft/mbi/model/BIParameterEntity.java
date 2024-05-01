package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bi_parameter", schema = "biserver", catalog = "BISERVER")
public class BIParameterEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "parameter_code", nullable = false)
    private int parameterCode;

    @Basic
    @Column(name = "parameter_value", nullable = false, length = 40)
    private String parameterValue;

}
