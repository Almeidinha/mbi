package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_slideshow_default", schema = "dbo", catalog = "BISERVER")
@IdClass(BIUserSlideshowDefaultPK.class)
public class BIUserSlideshowDefaultEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "slideshow_id", nullable = false)
    private int slideshowId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

    @ManyToOne
    @JoinColumn(name = "slideshow_id", referencedColumnName = "id", nullable = false)
    private BISlideshowEntity biSlideshowBySlideshow;

}
