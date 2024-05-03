package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIAnalysisFieldDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BiAnalysisFieldIdDTO;
import com.msoft.mbi.model.BIAnalysisFieldEntity;
import com.msoft.mbi.model.BiAnalysisFieldId;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BIAnalysisFieldMapper {

    // TODO check if this maps the ID correctly
    BIAnalysisFieldMapper BI_ANALYSIS_FIELD_MAPPER = Mappers.getMapper(BIAnalysisFieldMapper.class);

    BIAnalysisFieldDTO biEntityToDTO(BIAnalysisFieldEntity entity);

    BIAnalysisFieldEntity dtoToEntity(BIAnalysisFieldDTO dto);

    List<BIAnalysisFieldDTO> entityListToDTO(List<BIAnalysisFieldEntity> entities);

    List<BIAnalysisFieldEntity> listDTOToEntityList(List<BIAnalysisFieldDTO> dtos);


}
