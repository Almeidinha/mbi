package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_restriction", schema = "biserver", catalog = "BISERVER")
@IdClass(BIUserRestrictionPK.class)
public class BIUserRestrictionEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "restriction_id", nullable = false)
    private int restrictionId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "write_permission", nullable = false, length = 1)
    private boolean writePermission;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "indicator_id", referencedColumnName = "indicator_id", nullable = false),
            @JoinColumn(name = "restriction_id", referencedColumnName = "restriction_id", nullable = false)
    })
    private BIRestrictionEntity biRestriction;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

}
