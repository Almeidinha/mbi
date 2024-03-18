package com.msoft.mbi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

@Getter
@Setter
@EqualsAndHashCode
public class BIAnalysisUserPK implements Serializable {

    @Column(name = "indicator_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int indicatorId;

    @Column(name = "user_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "last_access_date", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Date lastAccessDate;

    @Column(name = "last_access_hour", nullable = false, length = 8)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String lastAccessHour;

}
