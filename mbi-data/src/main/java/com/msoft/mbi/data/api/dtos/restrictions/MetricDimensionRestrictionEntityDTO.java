package com.msoft.mbi.data.api.dtos.restrictions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricDimensionRestrictionEntityDTO {

    private int indicatorId;
    private int metricId;
    private int dimensionId;
}
