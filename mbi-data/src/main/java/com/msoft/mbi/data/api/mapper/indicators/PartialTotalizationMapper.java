package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.data.indicator.PartialTotalization;
import com.msoft.mbi.data.api.dtos.indicators.PartialTotalizationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartialTotalizationMapper {

    PartialTotalizationMapper PARTIAL_TOTALIZATION_MAPPER = Mappers.getMapper(PartialTotalizationMapper.class);

    PartialTotalizationDTO biEntityToDTO(PartialTotalization entity);

    PartialTotalization dtoToEntity(PartialTotalizationDTO dto);

    List<PartialTotalizationDTO> entityListToDTO(List<PartialTotalization> entities);

    List<PartialTotalization> setDTOToEntityList(List<PartialTotalizationDTO> dtos);
}
