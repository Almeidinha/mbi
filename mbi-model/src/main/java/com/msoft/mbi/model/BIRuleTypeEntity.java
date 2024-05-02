package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_rule_type", schema = "biserver", catalog = "biserver")
public class BIRuleTypeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "description", length = 50)
    private String description;

    @OneToMany(mappedBy = "biRuleTypeByRuleType")
    private Collection<BIRuleTypeActionEntity> biRuleTypeByRuleType;
}
