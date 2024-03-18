package com.msoft.mbi.model;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bi_sql_metric_filters", schema = "dbo", catalog = "BISERVER")
public class BISqlMetricFiltersEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "sq_text", length = 4000)
    private String sqlText;

    @OneToOne(mappedBy = "biIndSqlMetricFilter", fetch = FetchType.LAZY)
    private BIIndEntity biIndEntity;
}
