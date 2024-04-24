package com.msoft.mbi.data.api.dtos.restrictions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricDimensionRestrictionDTO {

    private int metricId;
    private List<Integer> dimensionIds;
}
