package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_group_panel", schema = "biserver", catalog = "BISERVER")
@IdClass(BIUserGroupPanelPK.class)
public class BIUserGroupPanelEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "panel_id", nullable = false)
    private int panelId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_group_id", nullable = false)
    private int userGroupId;

    @Basic
    @Column(name = "can_edit", nullable = false, length = 1)
    private boolean canEdit;

    @ManyToOne
    @JoinColumn(name = "panel_id", referencedColumnName = "id", nullable = false)
    private BIPanelEntity biPanelByPanel;

    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

}
