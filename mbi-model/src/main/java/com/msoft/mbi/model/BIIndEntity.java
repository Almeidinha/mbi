package com.msoft.mbi.model;

import lombok.*;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "bi_ind", schema = "biserver", catalog = "biserver",
        indexes = @Index(name = "ix1_bi_ind", columnList = "name")
)
public class BIIndEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Basic
    @Column(name = "file_name", length = 1)
    private String fileName;

    @Basic
    @Column(name = "graph_title", length = 100)
    private String graphTitle;

    @Basic
    @Column(name = "scheduled")
    private boolean scheduled;

    @Basic
    @Column(name = "default_graph")
    private int defaultGraph;

    @Basic
    @Column(name = "last_updated_user", length = 20)
    private int lastUpdatedUser;

    @Basic
    @Column(name = "comment", length = 1)
    private String comment;

    @Basic
    @Column(name = "is_frozen")
    private boolean frozen;

    @Basic
    @Column(name = "default_display", length = 1)
    private String defaultDisplay;

    @Basic
    @Column(name = "connection_id", nullable = false)
    private UUID connectionId;

    @Basic
    @Column(name = "number_of_steps")
    private Integer numberOfSteps;

    @Basic
    @Column(name = "uses_sequence")
    private boolean usesSequence;

    @Basic
    @Column(name = "table_type", nullable = false)
    private Integer tableType;

    @Basic
    @Column(name = "original_indicator")
    private Integer originalIndicator;

    @Basic
    @Column(name = "inherits_fields", length = 1)
    private boolean inheritsFields;

    @Basic
    @Column(name = "inherits_restriction", length = 1)
    private boolean inheritsRestrictions;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private BIAreaEntity biAreaByArea;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private BICompanyEntity companyIdByCompany;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "search_clause")
    private BISearchClauseEntity biSearchClause;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "from_clause")
    private BIFromClauseEntity biFromClause;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "where_clause")
    private BIWhereClauseEntity biWhereClause;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "fixed_condition_clause")
    private BIFixedConditionClauseEntity biFixedConditionClause;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "dimension_filter")
    private BIDimensionFilterEntity biDimensionFilter;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "metric_filter")
    private BIMetricFilterEntity biIndMetricFilter;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "sql_metric_filter")
    private BISqlMetricFiltersEntity biIndSqlMetricFilter;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "group_clause")
    private BIGroupClauseEntity biGroupClause;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "order_clause")
    private BIOrderClauseEntity biOrderClause;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "having_clause")
    private BIHavingClauseEntity havingClause;

    @OneToMany(mappedBy = "indicator", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<BIAnalysisFieldEntity> biAnalysisFields;

    @OneToMany(mappedBy = "biIndByInd", fetch = FetchType.LAZY)
    private List<BIUserIndEntity> biUserIndicators;

    @OneToMany(mappedBy = "biIndByInd", fetch = FetchType.LAZY)
    private List<BIUserGroupIndEntity> biUserGroupIndicators;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIScheduleEntity> biSchedule;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIGraphAlertEntity> biGraphAlerts;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIIndAlertColorEntity> biIndAlertColors;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BICnfGraphIndEntity> biCnfGraphIndicators;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BICommentsEntity> biComments;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIColorConditionsEntity> biColorConditions;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIDocumentIndicatorEntity> biDocumentIndicators;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIAnalysisGraphEntity> biAnalysisGraphs;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIAnalysisUserAccessEntity> biAnalysisUserAccess;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIPanelIndicatorEntity> biPanelIndicators;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIMeasureParEntity> biMeasurePars;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIAnalysisParameterEntity> biAnalysisParameters;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIUserGroupRestrictionEntity> biUserGroupRestrictions;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIDimMetricRestrictionEntity> biDimMetricRestrictions;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIUserRestrictionEntity> biUserRestrictions;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIRestrictionEntity> biRestrictions;

    @OneToMany(mappedBy = "biIndByInd")
    private List<BIUserDefaultIndicatorEntity> biUserDefaultIndicators;

    public void addField(BIAnalysisFieldEntity biAnalysisField) {
        if (this.biAnalysisFields == null) {
            this.biAnalysisFields = new ArrayList<>();
        }
        biAnalysisField.setIndicator(this);
        this.biAnalysisFields.add(biAnalysisField);

    }

    public void removeField(BIAnalysisFieldEntity biAnalysisField) {
        if (this.biAnalysisFields != null) {
            this.biAnalysisFields.remove(biAnalysisField);
            biAnalysisField.setIndicator(null);
        }
    }

    public void updateField(BIAnalysisFieldEntity biAnalysisField) {
        if (this.biAnalysisFields != null) {
            Optional<BIAnalysisFieldEntity> entityField = this.biAnalysisFields.stream()
                    .filter(field -> field.getId().equals(biAnalysisField.getId())).findFirst();

            if (entityField.isPresent()) {
                entityField.get().setDefaultField(biAnalysisField.getDefaultField());
                entityField.get().setVisualizationSequence(biAnalysisField.getVisualizationSequence());
                entityField.get().setLocalApres(biAnalysisField.getLocalApres());
            }

        }
    }

}
