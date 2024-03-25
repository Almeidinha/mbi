package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.*;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.api.mapper.filters.FiltersMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {FiltersMapper.class, PartialTotalizationsMapper.class, FieldMapper.class}
)
public interface IndicatorMapper {

    IndicatorMapper INDICATOR_MAPPER = Mappers.getMapper(IndicatorMapper.class);

    IndicatorDTO indicatorToDTO(Indicator entity);

    @Mappings({
            @Mapping(target = "fields", dependsOn = {"code"}),
            @Mapping(target = "filters", dependsOn = {"fields", "code"}),
            @Mapping(target = "dimensionFilters", dependsOn = {"fields", "filters"}),
            @Mapping(target = "metricSqlFilters", dependsOn = {"fields", "filters"}),
            @Mapping(target = "metricFilters", dependsOn = {"fields", "filters"}),
    })
    Indicator dtoToIndicator(IndicatorDTO dto) throws BIException;

    List<IndicatorDTO> indicatorListToDTO(List<Indicator> indicators);

    @Mappings({
            @Mapping(target = "fields", dependsOn = {"code"}),
            @Mapping(target = "filters", dependsOn = {"fields", "code"}),
            @Mapping(target = "dimensionFilters", dependsOn = {"fields", "filters"}),
            @Mapping(target = "metricSqlFilters", dependsOn = {"fields", "filters"}),
            @Mapping(target = "metricFilters", dependsOn = {"fields", "filters"}),
    })
    List<Indicator> setDTOToIndicatorList(List<IndicatorDTO> dtos);

}
