package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "bi_color_conditions", schema = "dbo", catalog = "BISERVER")
@IdClass(BIColorConditionsPK.class)
public class BIColorConditionsEntity {

    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @Id
    @Column(name = "field_id", nullable = false)
    private int fieldId;

    @Id
    @GeneratedValue
    @Column(name = "initial_value", nullable = false, precision = 6)
    private BigDecimal initialValue;

    @Id
    @GeneratedValue
    @Column(name = "final_value", nullable = false, precision = 6)
    private BigDecimal finalValue;

    @Basic
    @Column(name = "class_description", nullable = false, length = 40)
    private String classDescription;

    @Basic
    @Column(name = "font_color", length = 7)
    private String fontColor;

    @Basic
    @Column(name = "background_color", length = 7)
    private String backgroundColor;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "field_id", referencedColumnName = "field_id", nullable = false),
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false)
    })
    private BIAnalysisFieldEntity biAnalysisField;

}
