package com.msoft.mbi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_fixed_condition_clause", schema = "dbo", catalog = "BISERVER")
public class BIFixedConditionClauseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "sql_text", length = 4000)
    private String sqlText;

    @OneToOne(mappedBy = "biFixedConditionClause", fetch = FetchType.LAZY)
    private BIIndEntity biIndEntity;

}
