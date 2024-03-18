package com.msoft.mbi.data.api.dtos.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionDTO {

    protected Map<Integer, Object> valuesMap = new HashMap<>();
    private int valueCount = 0;
    private FieldDTO field;
    private OperatorDTO operator;
    private String value;
    private String title;
    private String SQLValue;
}
