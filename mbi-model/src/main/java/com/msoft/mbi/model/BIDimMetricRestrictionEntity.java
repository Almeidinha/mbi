package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_dim_metric_restriction", schema = "dbo", catalog = "BISERVER")
@IdClass(BIDimMetricRestrictionPK.class)
public class BIDimMetricRestrictionEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "metric_id", nullable = false)
    private int metricId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "dimension_id", nullable = false)
    private int dimensionId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "metric_id", referencedColumnName = "field_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false, insertable = false, updatable = false)
    })
    private BIAnalysisFieldEntity biAnalysisFieldMetricRestriction;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "dimension_id", referencedColumnName = "field_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false, insertable = false, updatable = false)
    })
    private BIAnalysisFieldEntity biAnalysisFieldDimensionRestriction;

}
