package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_rule", schema = "biserver", catalog = "biserver")
@IdClass(BIUserRulePK.class)
public class BIUserRuleEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule_type_id", nullable = false)
    private int ruleTypeId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule_type_action_id", nullable = false)
    private int ruleTypeActionId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "rule", nullable = false)
    private int rule;

    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

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
