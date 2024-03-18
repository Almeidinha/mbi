package com.msoft.mbi.data.api.dtos.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterFunctionDTO {

    private OperatorDTO operator;
    private String value;
    private int type;
    private FieldDTO field;
    private int amount;
    private boolean ranking;
    
}
