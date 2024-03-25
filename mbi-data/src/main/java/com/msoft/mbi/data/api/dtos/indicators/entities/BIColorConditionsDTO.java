package com.msoft.mbi.data.api.dtos.indicators.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BIColorConditionsDTO {

    private int indicatorId;

    private int fieldId;

    private BigDecimal initialValue;

    private BigDecimal finalValue;

    private String classDescription;

    private String fontColor;

    private String backgroundColor;
}
