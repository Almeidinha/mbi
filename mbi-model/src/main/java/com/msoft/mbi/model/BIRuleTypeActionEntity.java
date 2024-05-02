package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_rule_type_action", schema = "biserver", catalog = "biserver")
@IdClass(BIRuleTypeActionPK.class)
public class BIRuleTypeActionEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule_type_id", nullable = false)
    private int ruleTypeId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule_type_action_id", nullable = false)
    private int ruleTypeActionId;

    @Basic
    @Column(name = "description", length = 50)
    private String description;

    @OneToMany(mappedBy = "biRuleTypeByRuleType")
    private Collection<BIGroupRuleEntity> biAreaRules;

    @ManyToOne
    @JoinColumn(name = "rule_type_id", referencedColumnName = "id", nullable = false)
    private BIRuleTypeEntity biRuleTypeByRuleType;

    @OneToMany(mappedBy = "biRuleTypeByRuleType")
    private Collection<BIUserRuleEntity> biRuleTypeAction;

}
