package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_analysis_parameter", schema = "dbo", catalog = "BISERVER")
@IdClass(BIAnalysisParameterPK.class)
public class BIAnalysisParameterEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "parameter_id", nullable = false)
    private int parameterId;

    @Basic
    @Column(name = "description", nullable = false, length = 40)
    private String description;

    @Basic
    @Column(name = "value", nullable = false, length = 40)
    private String value;

    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIUserEntity biUserByUser;

}
