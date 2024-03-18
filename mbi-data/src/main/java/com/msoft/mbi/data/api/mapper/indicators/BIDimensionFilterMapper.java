package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.dtos.indicators.BIDimensionFilterDTO;
import com.msoft.mbi.model.BIDimensionFilterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIDimensionFilterMapper {

    BIDimensionFilterMapper BI_DIMENSION_FILTER_MAPPER = Mappers.getMapper(BIDimensionFilterMapper.class);

    BIDimensionFilterDTO biEntityToDTO(BIDimensionFilterEntity entity);

    BIDimensionFilterEntity dtoToEntity(BIDimensionFilterDTO dto);

    Set<BIDimensionFilterDTO> setEntityToDTO(Set<BIDimensionFilterEntity> entities);

    Set<BIDimensionFilterEntity> setDTOToEntity(Set<BIDimensionFilterDTO> dtos);
}
