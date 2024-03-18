package com.msoft.mbi.data.api.dtos.indicators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BIIndAlertColorDTO {

    private int alertSequence;

    private int indicatorId;

    private int firstFieldId;

    private String firstFieldFunction;

    private String operator;

    private String valueType;

    private String firstValueReference;

    private String secondValueReference;

    private Integer secondFiled;

    private String secondFiledFunction;

    private String action;

    private String fontName;

    private Integer fontSize;

    private String fontStyle;

    private String fontColor;

    private String backgroundColor;
}
