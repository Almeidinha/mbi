package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.dtos.indicators.BISearchClauseDTO;
import com.msoft.mbi.model.BISearchClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BISearchClauseMapper {

    BISearchClauseMapper BI_SEARCH_CLAUSE_MAPPER = Mappers.getMapper(BISearchClauseMapper.class);

    BISearchClauseDTO biEntityToDTO(BISearchClauseEntity entity);

    BISearchClauseEntity dtoToEntity(BISearchClauseDTO dto);

    Set<BISearchClauseDTO> setEntityToDTO(Set<BISearchClauseEntity> entities);

    Set<BISearchClauseEntity> setDTOToEntity(Set<BISearchClauseDTO> dtos);
}
