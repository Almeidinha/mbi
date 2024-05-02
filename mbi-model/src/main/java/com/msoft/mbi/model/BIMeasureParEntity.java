package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_measure_par", schema = "biserver", catalog = "biserver")
@IdClass(BIMeasureParPK.class)
public class BIMeasureParEntity {
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
    @Column(name = "field_id", nullable = false)
    private int fieldId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "sequence_value", nullable = false)
    private int sequenceValue;

    @Basic
    @Column(name = "value", nullable = false, length = 100)
    private String value;

    @Basic
    @Column(name = "color", length = 7)
    private String color;

    @Basic
    @Column(name = "font", length = 30)
    private String font;

    @Basic
    @Column(name = "font_size")
    private Integer fontSize;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false),
            @JoinColumn(name = "graph_id", referencedColumnName = "graph_id", nullable = false)
    })
    private BIAnalysisGraphEntity biAnalysisGraph;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

}
