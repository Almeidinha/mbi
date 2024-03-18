package com.msoft.mbi.data.api.dtos.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricFilterDTO {

    private ConditionDTO condition;

}
