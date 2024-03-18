package com.msoft.mbi.data.api.dtos.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltersDTO {

    private DimensionFilterDTO dimensionFilter;
    private MetricFiltersDTO metricFilters;
    private MetricSqlFilterDTO metricSqlFilter;
}
