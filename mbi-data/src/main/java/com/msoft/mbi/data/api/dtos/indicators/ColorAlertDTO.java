package com.msoft.mbi.data.api.dtos.indicators;

import com.msoft.mbi.data.api.dtos.filters.OperatorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorAlertDTO {

    private int sequence;
    private FieldDTO firstField;
    private String firstFieldFunction;
    private OperatorDTO operator;
    private String firstValue;
    private String secondValue;
    private String valueType;
    private FieldDTO secondField;
    private String secondFieldFunction;
    private String action;
    private AlertPropertyDTO alertProperty = new AlertPropertyDTO();
    private boolean compareToAnotherField;

}
