package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_graph_par", schema = "biserver", catalog = "BISERVER")
@IdClass(BIGraphParPK.class)
public class BIGraphParEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "graph_id", nullable = false)
    private int graphId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "graph_type", nullable = false)
    private int graphType;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "parameter_description", nullable = false, length = 40)
    private String parameterDescription;

    @Basic
    @Column(name = "parameter_value", length = 100)
    private String parameterValue;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false),
            @JoinColumn(name = "graph_id", referencedColumnName = "graph_id", nullable = false)
    })
    private BIAnalysisGraphEntity biAnalysisGraph;

}
