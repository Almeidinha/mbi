package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.filters.*;
import com.msoft.mbi.data.api.data.filters.TextCondition;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FiltersMapper {

    FiltersMapper FILTERS_MAPPER = Mappers.getMapper(FiltersMapper.class);

    FiltersDTO filterToDTO(Filters filters);

    Filters dtoToFilter(FiltersDTO dto);

    List<FiltersDTO> FilterListToDTO(List<Filters> filters);

    List<Filters> dtoListToFilters(List<FiltersDTO> dtos);

    default DimensionFilter createDimensionFilter() {
        return new DimensionTextFilter();
    }

    default MetricFilter createMetricFilter() {
        return new MetricTextFilter();
    }

    default Condition createCondition() {
        return new TextCondition();
    }

}
