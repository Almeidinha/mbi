package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.DimensionTextFilter;
import com.msoft.mbi.data.api.data.filters.TextCondition;
import com.msoft.mbi.data.api.dtos.filters.DimensionFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DimensionFilterMapper {

    DimensionFilterMapper DIMENSION_FILTER_MAPPER = Mappers.getMapper(DimensionFilterMapper.class);

    DimensionFilterDTO biDimensionFilterToDTO(DimensionFilter dimensionFilter);

    DimensionFilter dtoToDimensionFilter(DimensionFilterDTO dto);

    List<DimensionFilterDTO> listDimensionFilterToDTO(List<DimensionFilter> dimensionFilters);

    List<DimensionFilter> listDTOToDimensionFilter(List<DimensionFilterDTO> dtos);

    default DimensionFilter createDimensionFilter() {
        return new DimensionTextFilter();
    }

    default Condition createCondition() {
        return new TextCondition();
    }
}
