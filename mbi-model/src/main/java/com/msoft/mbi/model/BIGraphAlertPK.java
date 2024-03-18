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
public class BIGraphAlertPK implements Serializable {

    @Column(name = "indicator_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int indicatorId;

    @Column(name = "graph_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int graphId;

    @Column(name = "alert_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int alertId;

    @Column(name = "is_mark", nullable = false, length = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String isMark;

}
