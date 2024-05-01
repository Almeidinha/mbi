package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_documents", schema = "biserver", catalog = "BISERVER")
@IdClass(BIUserDocumentsPK.class)
public class BIUserDocumentsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "document_id", nullable = false)
    private int documentId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Basic
    @Column(name = "allow_modification", nullable = false)
    private boolean allowModification;

    @ManyToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id", nullable = false)
    private BIDocumentsEntity biDocumentByDocument;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

}
