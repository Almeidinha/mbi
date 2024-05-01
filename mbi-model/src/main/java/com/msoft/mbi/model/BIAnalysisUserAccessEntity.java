package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "bi_analysis_user_access", schema = "biserver", catalog = "BISERVER")
@IdClass(BIAnalysisUserPK.class)
public class BIAnalysisUserAccessEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "last_access_date", nullable = false)
    private Date lastAccessDate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "last_access_hour", nullable = false, length = 8)
    private String lastAccessHour;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

}
