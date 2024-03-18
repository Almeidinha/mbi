package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.dtos.indicators.BIIndLogicDTO;
import com.msoft.mbi.model.BIIndEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BIIndLogicToEntityMapper {

    BIIndLogicToEntityMapper BI_IND_LOGIC_MAPPER = Mappers.getMapper(BIIndLogicToEntityMapper.class);

    BIIndLogicDTO biEntityToDTO(BIIndEntity entity);

    BIIndEntity dtoToEntity(BIIndLogicDTO dto);

    List<BIIndLogicDTO> setEntityToDTO(List<BIIndEntity> entities);

    List<BIIndEntity> setDTOToEntity(List<BIIndLogicDTO> dtos);
}
