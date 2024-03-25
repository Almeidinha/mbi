package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIIndDTO;
import com.msoft.mbi.model.BIIndEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BIIndMapper {

    BIIndMapper BI_IND_MAPPER = Mappers.getMapper(BIIndMapper.class);

    BIIndDTO biEntityToDTO(BIIndEntity entity);

    BIIndEntity dtoToEntity(BIIndDTO dto);

    List<BIIndDTO> entityListToDTO(List<BIIndEntity> entities);

    List<BIIndEntity> setDTOToEntityList(List<BIIndDTO> dtos);
}
