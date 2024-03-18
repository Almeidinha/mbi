package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.dtos.indicators.BIColorConditionsDTO;
import com.msoft.mbi.model.BIColorConditionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIColorConditionsMapper {

    BIColorConditionsMapper BI_COLOR_CONDITIONS_MAPPER = Mappers.getMapper(BIColorConditionsMapper.class);

    BIColorConditionsDTO biEntityToDTO(BIColorConditionsEntity entity);

    BIColorConditionsEntity dtoToEntity(BIColorConditionsDTO dto);

    Set<BIColorConditionsDTO> setEntityToDTO(Set<BIColorConditionsEntity> entities);

    Set<BIColorConditionsEntity> setDTOToEntity(Set<BIColorConditionsDTO> dtos);
}
