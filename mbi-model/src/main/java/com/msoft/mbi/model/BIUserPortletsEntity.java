package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bi_user_portlets", schema = "dbo", catalog = "BISERVER")
@IdClass(BIUserPortletsPK.class)
public class BIUserPortletsEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "portlet_id", nullable = false)
    private int portletId;

    @Basic
    @Column(name = "portlet_order")
    private Integer portletOrder;

}
