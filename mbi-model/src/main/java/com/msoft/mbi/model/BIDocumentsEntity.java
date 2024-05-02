package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Date;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_documents", schema = "biserver", catalog = "biserver")
public class BIDocumentsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "description", length = 250)
    private String description;
    @Basic
    @Column(name = "tittle", nullable = false, length = 50)
    private String tittle;
    @Basic
    @Column(name = "expiration_date")
    private Date expirationDate;
    @Basic
    @Column(name = "creator", nullable = false, length = 50)
    private String creator;
    @Basic
    @Column(name = "id_restricted", length = 1)
    private boolean isRestricted;
    @Basic
    @Column(name = "created_by")
    private Integer createdBy;
    @OneToMany(mappedBy = "biDocumentByDocument")
    private Collection<BIUserGroupDocumentsEntity> biUserGroupDocumentsByDocuments;
    @OneToMany(mappedBy = "biDocumentByDocument")
    private Collection<BIDocumentIndicatorEntity> biDocumentIndicatorsByDocuments;
    @OneToMany(mappedBy = "biDocumentByDocument")
    private Collection<BIUserDocumentsEntity> biUserDocumentsByDocuments;

}
