package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_group_interface", schema = "biserver", catalog = "BISERVER")
@IdClass(BIUserGroupInterfacePK.class)
public class BIUserGroupInterfaceEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_group_id", nullable = false)
    private int userGroupId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "interface_id", nullable = false)
    private int interfaceId;

    @Basic
    @Column(name = "permission_level", nullable = false)
    private int permissionLevel;

    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

    @ManyToOne
    @JoinColumn(name = "interface_id", referencedColumnName = "id", nullable = false)
    private BIInterfaceEntity biInterfaceByInterface;

}
