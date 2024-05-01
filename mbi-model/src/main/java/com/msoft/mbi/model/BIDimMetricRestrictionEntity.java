package com.msoft.mbi.model;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_dim_metric_restriction", schema = "biserver", catalog = "BISERVER")
@IdClass(BIDimMetricRestrictionPK.class)
public class BIDimMetricRestrictionEntity {

    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @Id
    @Column(name = "metric_id", nullable = false)
    private int metricId;

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
