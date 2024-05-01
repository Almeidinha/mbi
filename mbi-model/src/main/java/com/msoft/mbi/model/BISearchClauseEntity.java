package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_search_clause", schema = "biserver", catalog = "BISERVER")
public class BISearchClauseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "sql_text", length = 2500)
    private String sqlText;

    @OneToOne(mappedBy = "biSearchClause", fetch = FetchType.LAZY)
    private BIIndEntity biIndEntity;
}
