package com.msoft.mbi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class BiGrpUsuAreaEntityPK implements Serializable {

    @Column(name = "grupo_usuario", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int grupoUsuario;
    @Column(name = "area", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int area;

}
