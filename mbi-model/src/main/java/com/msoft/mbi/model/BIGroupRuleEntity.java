package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_group_rule", schema = "biserver", catalog = "BISERVER")
@IdClass(BIGroupRulePK.class)
public class BIGroupRuleEntity {
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_group_id", nullable = false)
    private int userGroupId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule_type_id", nullable = false)
    private int ruleType;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule_type_action_id", nullable = false)
    private int ruleTypeActionId;

    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule", nullable = false)
    private int rule;

    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "rule_type_id", referencedColumnName = "rule_type_id", nullable = false),
            @JoinColumn(name = "rule_type_action_id", referencedColumnName = "rule_type_action_id", nullable = false)
    })
    private BIRuleTypeActionEntity biRuleTypeByRuleType;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIProductEntity biProductByProduct;

}
