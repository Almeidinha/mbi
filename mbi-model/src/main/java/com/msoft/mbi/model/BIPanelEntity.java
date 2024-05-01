package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_panel", schema = "biserver", catalog = "BISERVER")
public class BIPanelEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @OneToMany(mappedBy = "biPanelByPanel")
    private Collection<BIPanelIntercalationEntity> biPanelIntercalationsByPanel;

    @OneToMany(mappedBy = "biPanelByPanel")
    private Collection<BIUserGroupPanelEntity> biUserGroupPanelsByPanel;

    @OneToMany(mappedBy = "biPanelByPanel")
    private Collection<BIUserGroupDefaultPanelEntity> biUserGroupDefaultPanelsByPanel;

    @OneToMany(mappedBy = "biPanelByPanel")
    private Collection<BIPanelIndicatorEntity> biPanelIndicatorsByPanel;

    @OneToMany(mappedBy = "biPanelByPanel")
    private Collection<BIUserPanelEntity> biPanelUserAccessByPanel;

    @OneToMany(mappedBy = "biPanelByPanel")
    private Collection<BIPanelUserEntity> biPanelUsersByPanel;

    @OneToMany(mappedBy = "biPanelByPanel")
    private Collection<BIUserDefaultPanelEntity> biUserDefaultPanelsByPanel;
    
}
