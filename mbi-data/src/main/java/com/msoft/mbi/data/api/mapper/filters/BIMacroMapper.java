package com.msoft.mbi.data.api.mapper.filters;

import com.msoft.mbi.data.api.data.util.BIMacro;
import com.msoft.mbi.data.api.dtos.filters.BIMacroDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BIMacroMapper {

    BIMacroMapper BI_MACRO_MAPPER = Mappers.getMapper(BIMacroMapper.class);

    BIMacroDTO biBIMacroToDTO(BIMacro biMacro);

    BIMacro dtoToBIMacro(BIMacroDTO dto);

    List<BIMacroDTO> listBIMacroToDTO(List<BIMacro> biMacros);

    List<BIMacro> listDTOToBIMacro(List<BIMacroDTO> dtos);
}
