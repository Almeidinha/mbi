package com.msoft.mbi.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class BIUserGroupDocumentsPK implements Serializable {

    @Id
    @Basic
    @Column(name = "document_id", nullable = false)
    private int documentId;

    @Id
    @Basic
    @Column(name = "user_group_id", nullable = false)
    private int userGroupId;

}
