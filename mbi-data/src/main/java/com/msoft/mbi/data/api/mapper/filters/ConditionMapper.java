package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.filters.TextCondition;
import com.msoft.mbi.data.api.dtos.filters.ConditionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConditionMapper {

    ConditionMapper CONDITION_MAPPER = Mappers.getMapper(ConditionMapper.class);

    ConditionDTO biConditionToDTO(Condition condition);

    Condition dtoToCondition(ConditionDTO dto);

    List<ConditionDTO> listConditionToDTO(List<Condition> conditions);

    List<Condition> listDTOToCondition(List<ConditionDTO> dtos);

    default Condition createCondition() {
        return new TextCondition();
    }
}
