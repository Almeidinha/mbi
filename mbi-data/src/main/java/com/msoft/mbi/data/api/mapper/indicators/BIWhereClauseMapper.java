package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.dtos.indicators.BIWhereClauseDTO;
import com.msoft.mbi.model.BIWhereClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIWhereClauseMapper {

    BIWhereClauseMapper BI_WHERE_CLAUSE_MAPPER = Mappers.getMapper(BIWhereClauseMapper.class);

    BIWhereClauseDTO biEntityToDTO(BIWhereClauseEntity entity);

    BIWhereClauseEntity dtoToEntity(BIWhereClauseDTO dto);

    Set<BIWhereClauseDTO> setEntityToDTO(Set<BIWhereClauseEntity> entities);

    Set<BIWhereClauseEntity> setDTOToEntity(Set<BIWhereClauseDTO> dtos);
}
