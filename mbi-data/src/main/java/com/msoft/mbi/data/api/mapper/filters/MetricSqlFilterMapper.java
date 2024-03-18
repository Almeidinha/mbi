package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.filters.MetricFilter;
import com.msoft.mbi.data.api.data.filters.MetricSqlFilter;
import com.msoft.mbi.data.api.data.filters.MetricTextFilter;
import com.msoft.mbi.data.api.data.filters.TextCondition;
import com.msoft.mbi.data.api.dtos.filters.MetricSqlFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricSqlFilterMapper {

    MetricSqlFilterMapper METRIC_SQL_FILTER_MAPPER = Mappers.getMapper(MetricSqlFilterMapper.class);

    MetricSqlFilterDTO biMetricSqlFilterToDTO(MetricSqlFilter metricFilters);

    MetricSqlFilter dtoToMetricSqlFilter(MetricSqlFilterDTO dto);

    List<MetricSqlFilterDTO> listMetricSqlFilterToDTO(List<MetricSqlFilter> biMacros);

    List<MetricSqlFilter> listDTOToMetricSqlFilter(List<MetricSqlFilterDTO> dtos);

    default MetricFilter createMetricFilter() {
        return new MetricTextFilter();
    }

    default Condition createCondition() {
        return new TextCondition();
    }
}
