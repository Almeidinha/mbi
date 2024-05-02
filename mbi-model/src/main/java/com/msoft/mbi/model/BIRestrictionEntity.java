package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_restriction", schema = "biserver", catalog = "biserver")
@IdClass(BIRestrictionPK.class)
public class BIRestrictionEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "restriction_id", nullable = false)
    private int restrictionId;

    @Basic
    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Basic
    @Column(name = "expression", nullable = false, length = 4000)
    private String expression;

    @OneToMany(mappedBy = "biRestriction")
    private Collection<BIUserGroupRestrictionEntity> biUserGroupRestriction;

    @OneToMany(mappedBy = "biRestriction")
    private Collection<BIUserRestrictionEntity> biUserRestriction;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false)
    private BIIndEntity biIndByInd;

}
