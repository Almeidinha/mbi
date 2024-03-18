package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.dtos.indicators.BIFixedConditionClauseDTO;
import com.msoft.mbi.model.BIFixedConditionClauseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIFixedConditionClauseMapper {

    BIFixedConditionClauseMapper BI_FIXED_CONDITION_CLAUSE_MAPPER = Mappers.getMapper(BIFixedConditionClauseMapper.class);

    BIFixedConditionClauseDTO biEntityToDTO(BIFixedConditionClauseEntity entity);

    BIFixedConditionClauseEntity dtoToEntity(BIFixedConditionClauseDTO dto);

    Set<BIFixedConditionClauseDTO> setEntityToDTO(Set<BIFixedConditionClauseEntity> entities);

    Set<BIFixedConditionClauseEntity> setDTOToEntity(Set<BIFixedConditionClauseDTO> dtos);
}
