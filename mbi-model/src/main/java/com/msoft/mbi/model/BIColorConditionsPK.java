package com.msoft.mbi.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
@Getter
@Setter
@EqualsAndHashCode
public class BIColorConditionsPK implements Serializable {

    private int indicatorId;

    private int fieldId;

    private BigDecimal initialValue;

    private BigDecimal finalValue;

}
