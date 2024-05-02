package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "bi_display_version", schema = "biserver", catalog = "biserver")
public class BIDisplayVersionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "release_date")
    private Date releaseDate;

    @Basic
    @Column(name = "version_comment")
    private String versionComment;

    @Basic
    @Column(name = "implemented_version", length = 16)
    private String implementedVersion;

}
