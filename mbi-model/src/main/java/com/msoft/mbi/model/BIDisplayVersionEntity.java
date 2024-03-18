package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "bi_display_version", schema = "dbo", catalog = "BISERVER")
public class BIDisplayVersionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "release_date")
    private Date releaseDate;

    @Basic
    @Column(name = "last_conversion", length = 10)
    private String lastConversion;

    @Basic
    @Column(name = "version_comment")
    private String versionComment;

    @Basic
    @Column(name = "implemented_version", length = -1)
    private String implementedVersion;

    @Basic
    @Column(name = "version_correction", length = -1)
    private String versionCorrection;

}
