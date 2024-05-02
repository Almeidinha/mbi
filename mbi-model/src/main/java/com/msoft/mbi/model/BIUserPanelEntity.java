package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@Entity
@Table(name = "bi_user_panel", schema = "biserver", catalog = "biserver")
@IdClass(BIUserPanelPK.class)
public class BIUserPanelEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "panel_id", nullable = false)
    private int panelId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "last_access_date", nullable = false)
    private Date lastAccessDate;

    @Column(name = "last_access_hour", nullable = false, length = 8)
    private Time lastAccessHour;

    @ManyToOne
    @JoinColumn(name = "panel_id", referencedColumnName = "id", nullable = false)
    private BIPanelEntity biPanelByPanel;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

}
