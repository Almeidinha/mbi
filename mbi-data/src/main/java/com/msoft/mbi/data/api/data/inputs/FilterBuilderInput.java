package com.msoft.mbi.data.api.data.inputs;

import com.msoft.mbi.data.api.dtos.filters.FilterFunctionDTO;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.api.dtos.indicators.FieldDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilterBuilderInput {

    private FiltersDTO filters;
    private FilterFunctionDTO filtersFunction;
    private FieldDTO field;
    private String operator;
    private String value;
    private String link;
    private String connector;
}
