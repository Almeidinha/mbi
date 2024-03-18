package com.msoft.mbi.model;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bi_metric_filter", schema = "dbo", catalog = "BISERVER")
public class BIMetricFilterEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "sql_text", length = 4000)
    private String sqlText;

    @OneToOne(mappedBy = "biIndMetricFilter", fetch = FetchType.LAZY)
    private BIIndEntity biIndEntity;

}
