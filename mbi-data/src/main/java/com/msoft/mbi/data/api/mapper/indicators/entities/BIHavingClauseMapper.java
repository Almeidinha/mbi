package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIHavingClauseDTO;

import com.msoft.mbi.model.BIHavingClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIHavingClauseMapper {

    BIHavingClauseMapper BI_HAVING_CLAUSE_MAPPER = Mappers.getMapper(BIHavingClauseMapper.class);

    BIHavingClauseDTO biEntityToDTO(BIHavingClauseEntity entity);

    BIHavingClauseEntity dtoToEntity(BIHavingClauseDTO dto);

    Set<BIHavingClauseDTO> setEntityToDTO(Set<BIHavingClauseEntity> entities);

    Set<BIHavingClauseEntity> setDTOToEntity(Set<BIHavingClauseDTO> dtos);
}
