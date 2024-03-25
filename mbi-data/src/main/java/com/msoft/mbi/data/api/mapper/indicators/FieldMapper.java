package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.dtos.indicators.FieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FieldMapper {

    FieldMapper FIELD_MAPPER = Mappers.getMapper(FieldMapper.class);

    FieldDTO FieldToDTO(Field field);

    Field dtoToField(FieldDTO dto);

    List<FieldDTO> fieldListToDTO(List<Field> fields);

    List<Field> listDTOToFieldList(List<FieldDTO> dtos);
}
