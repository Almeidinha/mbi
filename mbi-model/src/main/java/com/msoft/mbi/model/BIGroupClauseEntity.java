package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bi_group_clause", schema = "biserver", catalog = "biserver")
public class BIGroupClauseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "sql_text", length = 2500)
    private String sqlText;

    @OneToOne(mappedBy = "biGroupClause", fetch = FetchType.LAZY)
    private BIIndEntity biIndEntity;

}
