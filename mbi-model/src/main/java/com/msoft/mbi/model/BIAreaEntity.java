package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_area", schema = "biserver", catalog = "biserver")
public class BIAreaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Basic
    @Column(name = "description", length = 30)
    private String description;

}
