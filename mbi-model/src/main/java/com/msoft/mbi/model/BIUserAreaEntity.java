package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "bi_user_area", schema = "dbo", catalog = "BISERVER")
@IdClass(BIUserAreaPK.class)
public class BIUserAreaEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "area_id", nullable = false)
    private int areaId;

}
