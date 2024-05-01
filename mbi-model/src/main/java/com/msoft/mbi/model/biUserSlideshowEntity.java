package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bi_user_slideshow", schema = "biserver", catalog = "BISERVER")
@IdClass(biUserSlideshowPK.class)
public class biUserSlideshowEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "slideshow_id", nullable = false)
    private int slideshowId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Basic
    @Column(name = "allow_modification", nullable = false)
    private boolean allowModification;

    @Basic
    @Column(name = "is_favorite")
    private Boolean isFavorite;

}
