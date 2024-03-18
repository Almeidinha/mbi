package com.msoft.mbi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class BIGroupRulePK implements Serializable {

    @Column(name = "user_group_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userGroupId;

    @Column(name = "rule_type_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ruleType;

    @Column(name = "rule_type_action_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ruleTypeActionId;

    @Column(name = "rule", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rule;

}
