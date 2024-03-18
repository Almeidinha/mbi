package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bi_having_clause", schema = "dbo", catalog = "BISERVER")
public class BIHavingClauseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "sql_text", length = 2500)
    private String sqlText;

    @OneToOne(mappedBy = "havingClause", fetch = FetchType.LAZY)
    private BIIndEntity biIndEntity;

}
