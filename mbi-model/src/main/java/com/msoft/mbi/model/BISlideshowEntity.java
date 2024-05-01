package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_slideshow", schema = "biserver", catalog = "BISERVER")
public class BISlideshowEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "description", nullable = false, length = 250)
    private String description;

    @Basic
    @Column(name = "display_logo", nullable = false, length = 1)
    private boolean displayLogo;

    @Basic
    @Column(name = "open_default", nullable = false, length = 1)
    private String openDefault;

    @OneToMany(mappedBy = "biSlideshowBySlideshow")
    private Collection<BIUserGroupSlideshowDefaultEntity> biUserGroupSlideshowDefaultsBySlideshow;

    @OneToMany(mappedBy = "biSlideshowBySlideshow")
    private Collection<BIUserSlideshowDefaultEntity> biUserSlideshowDefaultBySlideshow;

}
