package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_analysis_graph", schema = "biserver", catalog = "BISERVER")
@IdClass(BIAnalysisGraphPK.class)
public class BIAnalysisGraphEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "graph_id", nullable = false)
    private int graphId;

    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @Basic
    @Column(name = "left_coord")
    private Short leftCoord;

    @Basic
    @Column(name = "top_coord")
    private Short topCoord;
    @Basic
    @Column(name = "window_height")
    private Short windowHeight;

    @Basic
    @Column(name = "window_width")
    private Short windowWidth;

    @Basic
    @Column(name = "is_minimized")
    private boolean isMinimized;

    @OneToMany(mappedBy = "biAnalysisGraph")
    private Collection<BIGraphAlertEntity> biGraphAlert;

    @OneToMany(mappedBy = "biAnalysisGraph")
    private Collection<BIGraphFieldAnalysisEntity> biGraphFieldAnalysis;

    @OneToOne(mappedBy = "biAnalysisGraph")
    private BIAnalysisGraphDimFilterEntity biAnalysisGraphDimFilter;

    @OneToOne(mappedBy = "biAnalysisGraph")
    private BIAnalysisGraphMetFilterEntity biAnalysisGraphMetFilter;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false)
    private BIIndEntity biIndByInd;

    @OneToMany(mappedBy = "biAnalysisGraph")
    private Collection<BIGraphParEntity> biGraphPar;

    @OneToMany(mappedBy = "biAnalysisGraph")
    private Collection<BIMeasureParEntity> biMeasurePar;

}
