package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.dtos.filters.FieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FieldMapper {

    FieldMapper FIELD_MAPPER = Mappers.getMapper(FieldMapper.class);

    FieldDTO biFieldToDTO(Field field);

    Field dtoToField(FieldDTO dto);

    List<FieldDTO> listFieldToDTO(List<Field> fields);

    List<Field> listDTOToField(List<FieldDTO> dtos);
}
