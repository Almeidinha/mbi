package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "bi_graph_alert", schema = "biserver", catalog = "biserver")
@IdClass(BIGraphAlertPK.class)
public class BIGraphAlertEntity {
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
    @Column(name = "alert_id", nullable = false)
    private int alertId;

    @Id
    @Column(name = "is_mark", nullable = false, length = 1)
    private boolean isMark;

    @Basic
    @Column(name = "alert_title", length = 40)
    private String alertTitle;

    @Basic
    @Column(name = "alert_type", length = 1)
    private String alertType;

    @Basic
    @Column(name = "alert_color", length = 7)
    private String alertColor;

    @Basic
    @Column(name = "reference_axis", length = 1)
    private String referenceAxis;

    @Basic
    @Column(name = "initial_value", precision = 6)
    private BigDecimal initialValue;

    @Basic
    @Column(name = "final_value", precision = 6)
    private BigDecimal finalValue;

    @Basic
    @Column(name = "position", length = 1)
    private String position;

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
