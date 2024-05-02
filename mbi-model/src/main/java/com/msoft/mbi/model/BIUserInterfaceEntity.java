package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_interface", schema = "biserver", catalog = "biserver")
@IdClass(BIUserInterfacePK.class)
public class BIUserInterfaceEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "interface_id", nullable = false)
    private int interfaceId;

    @Basic
    @Column(name = "permission_level", nullable = false)
    private int permissionLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

    @ManyToOne
    @JoinColumn(name = "interface_id", referencedColumnName = "id", nullable = false)
    private BIInterfaceEntity biInterfaceByInterface;

}
