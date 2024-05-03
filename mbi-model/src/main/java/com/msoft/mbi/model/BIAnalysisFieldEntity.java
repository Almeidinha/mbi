package com.msoft.mbi.model;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@Entity
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "bi_analysis_field", schema = "biserver", catalog = "biserver",
        indexes = @Index(name = "ix2_bi_analysis_field", columnList = "title")
)
public class BIAnalysisFieldEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private BiAnalysisFieldId id = new BiAnalysisFieldId();

    @MapsId("indicatorId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "indicator_id", nullable = false)
    private BIIndEntity indicator;

    @Basic
    @Column(name = "name", length = 2000)
    private String name;

    @Basic
    @Column(name = "title", length = 100)
    private String title;

    @Basic
    @Column(name = "field_type", length = 1)
    private String fieldType;

    @Basic
    @Column(name = "data_type", length = 1)
    private String dataType;

    @Basic
    @Column(name = "field_nickname", length = 40)
    private String nickname;

    @Basic
    @Column(name = "is_expression", length = 1)
    private boolean expression;

    @Basic
    @Column(name = "filter_sequence")
    private Integer filterSequence;

    @Basic
    @Column(name = "visualization_sequence")
    private Integer visualizationSequence;

    @Basic
    @Column(name = "is_default", length = 1)
    private String defaultField;

    @Basic
    @Column(name = "field_order")
    private Integer fieldOrder;

    @Basic
    @Column(name = "table_nickname", length = 40)
    private String tableNickname;

    @Basic
    @Column(name = "direction", length = 4)
    private String direction;

    @Basic
    @Column(name = "decimal_position")
    private Integer decimalPositions;

    @Basic
    @Column(name = "field_totalization",length = 1)
    private String fieldTotalization;

    @Basic
    @Column(name = "vertical",length = 1)
    private String vertical;

    @Basic
    @Column(name = "aggregation_type", length = 15)
    private String aggregationType;

    @Basic
    @Column(name = "accumulated_participation")
    private boolean accumulatedParticipation;

    @Basic
    @Column(name = "field_color", length = 7)
    private String fieldColor;

    @Basic
    @Column(name = "default_graph", length = 1)
    private String defaultGraph;

    @Basic
    @Column(name = "ignore_zeros", length = 1)
    private String ignoreZeros;

    @Basic
    @Column(name = "accumulated_value")
    private boolean accumulatedValue;

    @Basic
    @Column(name = "local_apres")
    private Integer localApres;

    @Basic
    @Column(name = "column_width")
    private Integer columnWidth;

    @Basic
    @Column(name = "column_alignment", length = 1)
    private String columnAlignment;

    @Basic
    @Column(name = "horizontal")
    private String horizontal;

    @Basic
    @Column(name = "line_field_totalization")
    private boolean lineFieldTotalization;

    @Basic
    @Column(name = "accumulated_line_field", length = 1)
    private String accumulatedLineField;

    @Basic
    @Column(name = "tendency_line", length = 1)
    private String tendencyLine;

    @Basic
    @Column(name = "tendency_line_color", length = 7)
    private String tendencyLineColor;

    @Basic
    @Column(name = "date_mask", length = 20)
    private String dateMask;

    @Basic
    @Column(name = "partial_totalization")
    private boolean partialTotalization;

    @Basic
    @Column(name = "number_of_steps")
    private Integer numberOfSteps;

    @Basic
    @Column(name = "gantt_graph_position")
    private Integer ganttGraphPosition;

    @Basic
    @Column(name = "gantt_graph_color", length = 7)
    private String ganttGraphColor;

    @Basic
    @Column(name = "horizontal_participation")
    private boolean horizontalParticipation;

    @Basic
    @Column(name = "horizontal_participation_accumulated")
    private boolean horizontalParticipationAccumulated;

    @Basic
    @Column(name = "accumulated_order")
    private Integer accumulatedOrder;

    @Basic
    @Column(name = "accumulated_order_direction", length = 4)
    private String accumulatedOrderDirection;

    @Basic
    @Column(name = "uses_med_line")
    private boolean usesMediaLine;

    @Basic
    @Column(name = "is_fixed_value")
    private boolean fixedValue;

    @Basic
    @Column(name = "loc_apres_grapg")
    private Integer locApresGraph;

    @Basic
    @Column(name = "graph_type", length = 20)
    private String graphType;

    @Basic
    @Column(name = "first_graph_type", length = 20)
    private String firstGraphType;

    @Basic
    @Column(name = "sec_graph_type", length = 20)
    private String secGraphType;

    @Basic
    @Column(name = "reference_axis", length = 2)
    private String referenceAxis;

    @Basic
    @Column(name = "graph_visualization_sequence")
    private Integer graphVisualizationSequence;

    @Basic
    @Column(name = "original_analysis_field")
    private Integer originalAnalysisField;

    @Basic
    @Column(name = "is_drilldown")
    private boolean drillDown;

    @Basic
    @Column(name = "general_filter")
    private Integer generalFilter;

    @Basic
    @Column(name = "mandatory_filter")
    private boolean mandatoryFilter;

    @Basic
    @Column(name = "delegate_order")
    private Integer delegateOrder;

    @OneToMany(mappedBy = "biAnalysisFieldByFk2ColorAlertInd")
    private Collection<BIIndAlertColorEntity> biColorAlertInds1;

    @OneToMany(mappedBy = "biCampoAnaliseByFk3ColorAlertInd")
    private Collection<BIIndAlertColorEntity> biColorAlertInds2;

    @OneToMany(mappedBy = "biAnalysisField")
    private Collection<BIColorConditionsEntity> biColorConditions;

    @OneToMany(mappedBy = "biAnalysisFieldDimensionRestriction")
    private Collection<BIDimMetricRestrictionEntity> biDimDimRestriction;

    @OneToMany(mappedBy = "biAnalysisFieldMetricRestriction")
    private Collection<BIDimMetricRestrictionEntity> biDimMetricRestriction;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIAnalysisFieldEntity )) return false;
        return id != null && id.equals(((BIAnalysisFieldEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
