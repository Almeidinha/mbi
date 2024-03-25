package com.msoft.mbi.data.api.mapper.indicators.entities;


import com.msoft.mbi.data.api.dtos.indicators.entities.BIFromClauseDTO;
import com.msoft.mbi.model.BIFromClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIFromClauseMapper {

    BIWhereClauseMapper BI_WHERE_CLAUSE_MAPPER = Mappers.getMapper(BIWhereClauseMapper.class);

    BIFromClauseDTO biEntityToDTO(BIFromClauseEntity entity);

    BIFromClauseEntity dtoToEntity(BIFromClauseDTO dto);

    Set<BIFromClauseDTO> setEntityToDTO(Set<BIFromClauseEntity> entities);

    Set<BIFromClauseEntity> setDTOToEntity(Set<BIFromClauseDTO> dtos);
}
