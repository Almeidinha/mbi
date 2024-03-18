package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.filters.MetricFilter;
import com.msoft.mbi.data.api.data.filters.MetricTextFilter;
import com.msoft.mbi.data.api.data.filters.TextCondition;
import com.msoft.mbi.data.api.dtos.filters.MetricFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricFilterMapper {

    MetricFilterMapper METRIC_FILTER_MAPPER = Mappers.getMapper(MetricFilterMapper.class);

    MetricFilterDTO biMetricFilterToDTO(MetricFilter metricFilter);

    MetricFilter dtoToMetricFilter(MetricFilterDTO dto);

    List<MetricFilterDTO> listMetricFilterToDTO(List<MetricFilter> metricFilters);

    List<MetricFilter> listDTOToMetricFilter(List<MetricFilterDTO> dtos);

    default MetricFilter createMetricFilter() {
        return new MetricTextFilter();
    }

    default Condition createCondition() {
        return new TextCondition();
    }

}
