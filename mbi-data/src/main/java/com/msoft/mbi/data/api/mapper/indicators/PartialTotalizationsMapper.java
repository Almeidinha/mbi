package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.data.indicator.PartialTotalizations;
import com.msoft.mbi.data.api.dtos.indicators.PartialTotalizationsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface PartialTotalizationsMapper {

    PartialTotalizationsMapper PARTIAL_TOTALIZATIONS_MAPPER = Mappers.getMapper(PartialTotalizationsMapper.class);

    PartialTotalizationsDTO biEntityToDTO(PartialTotalizations entity);

    PartialTotalizations dtoToEntity(PartialTotalizationsDTO dto);

}
