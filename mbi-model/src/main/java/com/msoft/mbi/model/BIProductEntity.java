package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_product", schema = "biserver", catalog = "biserver")
public class BIProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Basic
    @Column(name = "description", length = 50)
    private String description;

    @OneToMany(mappedBy = "biProductByProduct")
    private Collection<BIGroupRuleEntity> biGroupRulesByProduct;

    @OneToMany(mappedBy = "biProductByProduct")
    private Collection<BIUserRuleEntity> biUserRuleByProduct;

}
