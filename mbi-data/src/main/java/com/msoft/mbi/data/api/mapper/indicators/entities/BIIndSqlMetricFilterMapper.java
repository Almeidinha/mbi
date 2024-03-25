package com.msoft.mbi.data.api.mapper.indicators.entities;


import com.msoft.mbi.data.api.dtos.indicators.entities.BIIndSqlMetricFilterDTO;
import com.msoft.mbi.model.BISqlMetricFiltersEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIIndSqlMetricFilterMapper {

    BIIndSqlMetricFilterMapper BI_IND_SQL_METRIC_FILTER_MAPPER = Mappers.getMapper(BIIndSqlMetricFilterMapper.class);

    BIIndSqlMetricFilterDTO biEntityToDTO(BISqlMetricFiltersEntity entity);

    BISqlMetricFiltersEntity dtoToEntity(BIIndSqlMetricFilterDTO dto);

    Set<BIIndSqlMetricFilterDTO> setEntityToDTO(Set<BISqlMetricFiltersEntity> entities);

    Set<BISqlMetricFiltersEntity> setDTOToEntity(Set<BIIndSqlMetricFilterDTO> dtos);
}
