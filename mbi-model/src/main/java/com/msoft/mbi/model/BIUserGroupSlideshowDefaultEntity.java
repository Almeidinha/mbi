package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_group_slideshow_default", schema = "biserver", catalog = "BISERVER")
@IdClass(BIUserGroupSlideshowDefaultPK.class)
public class BIUserGroupSlideshowDefaultEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_group_id", nullable = false)
    private int userGroupId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "slideshow_id", nullable = false)
    private int slideshowId;

    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

    @ManyToOne
    @JoinColumn(name = "slideshow_id", referencedColumnName = "id", nullable = false)
    private BISlideshowEntity biSlideshowBySlideshow;

}
