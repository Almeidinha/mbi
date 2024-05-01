package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_interface", schema = "biserver", catalog = "BISERVER")
public class BIInterfaceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Basic
    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @OneToMany(mappedBy = "biInterfaceByInterface")
    private Collection<BIInterfaceActionEntity> biInterfaceActionsByInterfaces;

    @OneToMany(mappedBy = "biInterfaceByInterface")
    private Collection<BIUserGroupInterfaceEntity> biUserGroupInterfacesByInterfaces;

    @OneToMany(mappedBy = "biInterfaceByInterface")
    private Collection<BIAccessRestrictionInterfaceEntity> biAccessRestrictionInterfacesByInterfaces;

    @OneToMany(mappedBy = "biInterfaceByInterface")
    private Collection<BIUserInterfaceEntity> biUserInterfacesByInterfaces;

}
