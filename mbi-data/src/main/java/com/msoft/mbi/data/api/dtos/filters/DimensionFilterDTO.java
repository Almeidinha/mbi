package com.msoft.mbi.data.api.dtos.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DimensionFilterDTO {

    private ArrayList<DimensionFilterDTO> filters;
    private String connector;
    private ConditionDTO condition;
    private BIMacroDTO macro;
    private FieldDTO macroField;
    private boolean drillDown = false;

}
