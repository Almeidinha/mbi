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
public class BIGraphParPK implements Serializable {

    @Column(name = "indicator_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int indicatorId;

    @Column(name = "graph_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int graphId;

    @Column(name = "graph_type", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int graphType;

    @Column(name = "parameter_description", nullable = false, length = 40)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String parameterDescription;

}
