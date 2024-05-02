package com.msoft.mbi.model;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_from_clause", schema = "biserver", catalog = "biserver")
public class BIFromClauseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "sql_text", length = 4000)
    private String sqlText;

    @OneToOne(mappedBy = "biFromClause")
    private BIIndEntity biIndEntity;

}



