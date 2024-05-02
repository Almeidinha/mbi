package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_group_documents", schema = "biserver", catalog = "biserver")
@IdClass(BIUserGroupDocumentsPK.class)
public class BIUserGroupDocumentsEntity {

    @Id
    @Basic
    @Column(name = "document_id", nullable = false)
    private int documentId;

    @Id
    @Basic
    @Column(name = "user_group_id", nullable = false)
    private int userGroupId;

    @Basic
    @Column(name = "can_edit", nullable = false, length = 1)
    private boolean canEdit;

    @ManyToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id", nullable = false)
    private BIDocumentsEntity biDocumentByDocument;

    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

}
