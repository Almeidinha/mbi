package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.filters.MetricFilter;
import com.msoft.mbi.data.api.data.filters.MetricFilters;
import com.msoft.mbi.data.api.data.filters.MetricTextFilter;
import com.msoft.mbi.data.api.data.filters.TextCondition;
import com.msoft.mbi.data.api.dtos.filters.MetricFiltersDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricFiltersMapper {

    MetricFiltersMapper METRIC_FILTERS_MAPPER = Mappers.getMapper(MetricFiltersMapper.class);

    MetricFiltersDTO biMetricFiltersToDTO(MetricFilters metricFilters);

    MetricFilters dtoToMetricFilters(MetricFiltersDTO dto);

    List<MetricFiltersDTO> listMetricFiltersToDTO(List<MetricFilters> metricFiltersList);

    List<MetricFilters> listDTOToMetricFilters(List<MetricFiltersDTO> dtos);

    default MetricFilter createMetricFilter() {
        return new MetricTextFilter();
    }

    default Condition createCondition() {
        return new TextCondition();
    }
}
