package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "bi_analysis_graph_met_filter", schema = "dbo", catalog = "BISERVER")
@IdClass(BiGrafAnlFiltroMetricaEntityPK.class)
public class BIAnalysisGraphMetFilterEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int ind;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "graph_id", nullable = false)
    private int grafico;
    @Basic
    @Column(name = "sql_text", length = 4000)
    private String sqlText;
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false),
            @JoinColumn(name = "graph_id", referencedColumnName = "graph_id", nullable = false)
    })
    private BIAnalysisGraphEntity biAnalysisGraph;

}
