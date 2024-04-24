package com.msoft.mbi.data.api.mapper.restrictions;

import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionDTO;
import com.msoft.mbi.model.BIDimMetricRestrictionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BIMetricRestrictionMapper {

    BIMetricRestrictionMapper BI_METRIC_RESTRICTION_MAPPER = Mappers.getMapper(BIMetricRestrictionMapper.class);

    MetricDimensionRestrictionDTO biEntityToDTO(BIDimMetricRestrictionEntity entity);

    BIDimMetricRestrictionEntity dtoToEntity(MetricDimensionRestrictionDTO dto);

    List<MetricDimensionRestrictionDTO> listEntityToDTOs(List<BIDimMetricRestrictionEntity> entities);

    List<BIDimMetricRestrictionEntity> listDTOToEntities(List<MetricDimensionRestrictionDTO> dtos);

}

