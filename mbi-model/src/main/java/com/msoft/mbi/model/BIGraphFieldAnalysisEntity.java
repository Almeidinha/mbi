package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_graph_field_analysis", schema = "biserver", catalog = "biserver")
@IdClass(BIGraphFieldAnalysisPK.class)
public class BIGraphFieldAnalysisEntity {

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
    @Column(name = "graph_field_id", nullable = false)
    private int graphFieldId;

    @Basic
    @Column(name = "ignore_zeros", length = 1)
    private String ignoreZeros;

    @Basic
    @Column(name = "tendency_line", length = 1)
    private String tendencyLine;

    @Basic
    @Column(name = "tendency_line_color", length = 7)
    private String tendencyLineColor;

    @Basic
    @Column(name = "gantt_position")
    private Integer ganttPosition;

    @Basic
    @Column(name = "grafico_color", length = 7)
    private String graficoColor;

    @Basic
    @Column(name = "loc_graph_pres")
    private Integer locGraphPres;

    @Basic
    @Column(name = "graph_mark_type", length = 20)
    private String graphMarkType;

    @Basic
    @Column(name = "primary_graph_type", length = 20)
    private String primaryGraphType;

    @Basic
    @Column(name = "secondary_graph_type", length = 20)
    private String secondaryGraphType;

    @Basic
    @Column(name = "reference_axis", length = 2)
    private String referenceAxis;

    @Basic
    @Column(name = "view_sequence")
    private Integer viewSequence;

    @Basic
    @Column(name = "field_name", length = 2000)
    private String fieldName;

    @Basic
    @Column(name = "field_title", length = 40)
    private String fieldTitle;

    @Basic
    @Column(name = "field_type", length = 1)
    private String fieldType;

    @Basic
    @Column(name = "field_nickname", length = 40)
    private String nickname;

    @Basic
    @Column(name = "data_type", length = 1)
    private String dataType;

    @Basic
    @Column(name = "field_color", length = 7)
    private String fieldColor;

    @Basic
    @Column(name = "field_order")
    private Integer fieldOrder;

    @Basic
    @Column(name = "order_direction", length = 4)
    private String orderDirection;

    @Basic
    @Column(name = "filter_sequence")
    private Integer filterSequence;

    @Basic
    @Column(name = "is_viewed")
    private boolean isViewed;

    @Basic
    @Column(name = "is_drill_down")
    private boolean isDrillDown;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false),
            @JoinColumn(name = "graph_id", referencedColumnName = "graph_id", nullable = false)
    })
    private BIAnalysisGraphEntity biAnalysisGraph;

}
