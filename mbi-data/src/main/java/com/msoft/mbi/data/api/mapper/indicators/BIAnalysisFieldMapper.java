package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.dtos.indicators.BIAnalysisFieldDTO;
import com.msoft.mbi.model.BIAnalysisFieldEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
public interface BIAnalysisFieldMapper {
    BIAnalysisFieldMapper BI_ANALYSIS_FIELD_MAPPER = Mappers.getMapper(BIAnalysisFieldMapper.class);

    BIAnalysisFieldDTO biEntityToDTO(BIAnalysisFieldEntity entity);

    BIAnalysisFieldEntity dtoToEntity(BIAnalysisFieldDTO dto);

    List<BIAnalysisFieldDTO> setEntityToDTO(List<BIAnalysisFieldEntity> entities);

    List<BIAnalysisFieldEntity> setDTOToEntity(List<BIAnalysisFieldDTO> dtos);
}
