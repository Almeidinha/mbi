package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "bi_interface_action", schema = "biserver", catalog = "biserver")
public class BIInterfaceActionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Basic
    @Column(name = "interface_id", nullable = false)
    private int interfaceId;

    @Basic
    @Column(name = "action_weight", nullable = false)
    private int actionWeight;

    @Basic
    @Column(name = "description", nullable = false, length = 40)
    private String description;

    @ManyToOne
    @JoinColumn(name = "interface_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIInterfaceEntity biInterfaceByInterface;

}
