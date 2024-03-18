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
public class BiGrafAnlFiltroMetricaEntityPK implements Serializable {

    @Column(name = "ind", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ind;
    @Column(name = "grafico", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int grafico;

}
