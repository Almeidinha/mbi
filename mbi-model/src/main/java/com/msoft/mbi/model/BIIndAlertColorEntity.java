package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_ind_alert_color", schema = "dbo", catalog = "BISERVER")
@IdClass(BIIndAlertColorPK.class)
public class BIIndAlertColorEntity {

    // @GeneratedValue(strategy = GenerationType.IDENTITY) is it tho?
    @Id
    @Column(name = "alert_sequence", nullable = false)
    private int alertSequence;

    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @Basic
    @Column(name = "first_field_id", nullable = false)
    private int firstFieldId;

    @Basic
    @Column(name = "first_field_function", nullable = false, length = 40)
    private String firstFieldFunction;

    @Basic
    @Column(name = "operator", nullable = false, length = 35)
    private String operator;

    @Basic
    @Column(name = "value_type", nullable = false, length = 1)
    private String valueType;

    @Basic
    @Column(name = "first_value_reference", nullable = false, length = 100)
    private String firstValueReference;

    @Basic
    @Column(name = "second_value_reference", length = 100)
    private String secondValueReference;

    @Basic
    @Column(name = "second_filed_id")
    private Integer secondFiled;

    @Basic
    @Column(name = "second_filed_function", length = 40)
    private String secondFiledFunction;

    @Basic
    @Column(name = "action", length = 25)
    private String action;

    @Basic
    @Column(name = "font_name", length = 40)
    private String fontName;

    @Basic
    @Column(name = "font_size")
    private Integer fontSize;

    @Basic
    @Column(name = "font_style", length = 8)
    private String fontStyle;

    @Basic
    @Column(name = "font_color", length = 8)
    private String fontColor;

    @Basic
    @Column(name = "background_color", length = 8)
    private String backgroundColor;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "first_field_id", referencedColumnName = "field_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false, insertable = false, updatable = false)
    })
    private BIAnalysisFieldEntity biAnalysisFieldByFk2ColorAlertInd;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "second_filed_id", referencedColumnName = "field_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false, insertable = false, updatable = false)
    })
    private BIAnalysisFieldEntity biCampoAnaliseByFk3ColorAlertInd;

}
