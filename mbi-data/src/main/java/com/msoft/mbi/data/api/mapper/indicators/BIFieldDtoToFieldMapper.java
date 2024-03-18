package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.dtos.filters.FieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BIFieldDtoToFieldMapper {

    BIFieldDtoToFieldMapper BI_FIELD_DTO_TO_FIELD_MAPPER  = Mappers.getMapper(BIFieldDtoToFieldMapper.class);

    FieldDTO fieldToDTO(Field field);

    Field dtoToField(FieldDTO dto);

    List<FieldDTO> fieldListToDTO(List<Field> Fields);

    List<Field> DTOListToFields(List<FieldDTO> dtos);
}

