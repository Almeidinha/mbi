package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.dtos.filters.OperatorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperatorMapper {

    OperatorMapper OPERATOR_MAPPER = Mappers.getMapper(OperatorMapper.class);

    OperatorDTO biOperatorToDTO(Operator operator);

    Operator dtoToBIMacro(OperatorDTO dto);

    List<OperatorDTO> listBIMacroToDTO(List<Operator> biMacros);

    List<Operator> listDTOToBIMacro(List<OperatorDTO> dtos);
}
