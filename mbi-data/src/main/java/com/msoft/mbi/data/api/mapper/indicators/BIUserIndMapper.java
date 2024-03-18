package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.dtos.indicators.BIUserIndDTO;

import com.msoft.mbi.model.BIUserIndEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserIndMapper {

    BIUserIndMapper BI_USER_IND_MAPPER = Mappers.getMapper(BIUserIndMapper.class);

    BIUserIndDTO biEntityToDTO(BIUserIndEntity entity);

    BIUserIndEntity dtoToEntity(BIUserIndDTO dto);

    Set<BIUserIndDTO> setEntityToDTO(Set<BIUserIndEntity> entities);

    Set<BIUserIndEntity> setDTOToEntity(Set<BIUserIndDTO> dtos);
}
