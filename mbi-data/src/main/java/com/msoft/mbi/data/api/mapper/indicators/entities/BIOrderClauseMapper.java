package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIOrderClauseDTO;
import com.msoft.mbi.model.BIOrderClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIOrderClauseMapper {

    BIOrderClauseMapper BI_ORDER_CLAUSE_MAPPER = Mappers.getMapper(BIOrderClauseMapper.class);

    BIOrderClauseDTO biEntityToDTO(BIOrderClauseEntity entity);

    BIOrderClauseEntity dtoToEntity(BIOrderClauseDTO dto);

    Set<BIOrderClauseDTO> setEntityToDTO(Set<BIOrderClauseEntity> entities);

    Set<BIOrderClauseEntity> setDTOToEntity(Set<BIOrderClauseDTO> dtos);
}
