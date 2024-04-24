package com.msoft.mbi.data.api.mapper.restrictions;

import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionEntityDTO;
import com.msoft.mbi.model.BIDimMetricRestrictionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetricDimensionRestrictionEntityMapper {

    MetricDimensionRestrictionEntityMapper METRIC_DIMENSION_RESTRICTION_ENTITY = Mappers.getMapper(MetricDimensionRestrictionEntityMapper.class);

    MetricDimensionRestrictionEntityDTO biEntityToDTO(BIDimMetricRestrictionEntity entity);

    BIDimMetricRestrictionEntity dtoToEntity(MetricDimensionRestrictionEntityDTO dto);

    List<MetricDimensionRestrictionEntityDTO> listEntityToDTOs(List<BIDimMetricRestrictionEntity> entities);

    List<BIDimMetricRestrictionEntity> listDTOToEntities(List<MetricDimensionRestrictionEntityDTO> dtos);
}
