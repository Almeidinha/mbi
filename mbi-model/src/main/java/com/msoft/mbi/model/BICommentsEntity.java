package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "bi_comments", schema = "biserver", catalog = "BISERVER")
@IdClass(BICommentsPK.class)
public class BICommentsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Basic
    @Column(name = "comment", nullable = false, length = 2000)
    private String comment;

    @Basic
    @Column(name = "send_by_email", length = 1)
    private String sendByEmail;

    @Basic
    @Column(name = "expire_date")
    private Date expireDate;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false)
    private BIIndEntity biIndByInd;

}
