package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_analysis_graph_dim_filter", schema = "biserver", catalog = "biserver")
@IdClass(BIAnalysisGraphDimFilterPK.class)
public class BIAnalysisGraphDimFilterEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "graph_id", nullable = false)
    private int graphId;
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
