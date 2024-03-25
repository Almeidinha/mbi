package com.msoft.mbi.data.api.mapper.indicators.entities;


import com.msoft.mbi.data.api.dtos.indicators.entities.BIGroupClauseDTO;
import com.msoft.mbi.model.BIGroupClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIGroupClauseMapper {

    BIGroupClauseMapper BI_GROUP_CLAUSE_MAPPER = Mappers.getMapper(BIGroupClauseMapper.class);

    BIGroupClauseDTO biEntityToDTO(BIGroupClauseEntity entity);

    BIGroupClauseEntity dtoToEntity(BIGroupClauseDTO dto);

    Set<BIGroupClauseDTO> setEntityToDTO(Set<BIGroupClauseEntity> entities);

    Set<BIGroupClauseEntity> setDTOToEntity(Set<BIGroupClauseDTO> dtos);
}
