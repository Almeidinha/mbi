package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.FieldDTO;
import com.msoft.mbi.model.BIAnalysisFieldEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIAnalysisFieldToFieldDTOMapper {

    BIAnalysisFieldToFieldDTOMapper BI_ANALYSIS_FIELD_TO_FIELD_MAPPER = Mappers.getMapper(BIAnalysisFieldToFieldDTOMapper.class);

    @Mappings({
            @Mapping(target = "verticalAnalysisType", source = "vertical"),
            @Mapping(target = "horizontalAnalysisType", source = "horizontal"),
            @Mapping(target = "drillDownSequence", source = "filterSequence"),
            @Mapping(target = "order", source = "fieldOrder"),
            @Mapping(target = "orderDirection", source = "direction"),
            @Mapping(target = "numDecimalPositions", source = "decimalPosition"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", source = "fieldTotalization"),
            @Mapping(target = "sumLine", source = "lineFieldTotalization"),
            @Mapping(target = "horizontalParticipationAccumulated", source = "accumulatedLineField"),
            @Mapping(target = "indicatorId", source = "biIndByInd.id"),
            @Mapping(target = "verticalAnalysis", expression = "java(!entity.getVertical().equals(\"N\"))"),
            @Mapping(target = "horizontalAnalysis", expression = "java(!entity.getHorizontal().equals(\"N\"))"),
    })
    FieldDTO biEntityToDTO(BIAnalysisFieldEntity entity);

    @Mappings({
            @Mapping(target = "vertical", source = "verticalAnalysisType"),
            @Mapping(target = "horizontal", source = "horizontalAnalysisType"),
            @Mapping(target = "filterSequence", source = "drillDownSequence"),
            @Mapping(target = "fieldOrder", source = "order"),
            @Mapping(target = "direction", source = "orderDirection"),
            @Mapping(target = "decimalPosition", source = "numDecimalPositions"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "fieldTotalization", source = "totalizingField"),
            @Mapping(target = "lineFieldTotalization", source = "sumLine"),
            @Mapping(target = "biIndByInd.id", source = "indicatorId"),
            @Mapping(target = "accumulatedLineField", source = "horizontalParticipationAccumulated"),

    })
    BIAnalysisFieldEntity dtoToEntity(FieldDTO dto);

    @Mappings({
            @Mapping(target = "verticalAnalysisType", source = "vertical"),
            @Mapping(target = "horizontalAnalysisType", source = "horizontal"),
            @Mapping(target = "drillDownSequence", source = "filterSequence"),
            @Mapping(target = "order", source = "fieldOrder"),
            @Mapping(target = "orderDirection", source = "direction"),
            @Mapping(target = "numDecimalPositions", source = "decimalPosition"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", source = "fieldTotalization"),
            @Mapping(target = "sumLine", source = "lineFieldTotalization"),
            @Mapping(target = "horizontalParticipationAccumulated", source = "accumulatedLineField"),
            @Mapping(target = "indicatorId", source = "biIndByInd.id"),
            @Mapping(target = "verticalAnalysis", expression = "java(!entity.getVertical().equals(\"N\"))"),
            @Mapping(target = "horizontalAnalysis", expression = "java(!entity.getHorizontal().equals(\"N\"))"),
    })
    List<FieldDTO> setEntityToDTO(List<BIAnalysisFieldEntity> entities);

    @Mappings({
            @Mapping(target = "vertical", source = "verticalAnalysisType"),
            @Mapping(target = "horizontal", source = "horizontalAnalysisType"),
            @Mapping(target = "filterSequence", source = "drillDownSequence"),
            @Mapping(target = "fieldOrder", source = "order"),
            @Mapping(target = "direction", source = "orderDirection"),
            @Mapping(target = "decimalPosition", source = "numDecimalPositions"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "fieldTotalization", source = "totalizingField"),
            @Mapping(target = "lineFieldTotalization", source = "sumLine"),
            @Mapping(target = "biIndByInd.id", source = "indicatorId"),
            @Mapping(target = "accumulatedLineField", source = "horizontalParticipationAccumulated"),

    })
    List<BIAnalysisFieldEntity> setDTOToEntity(List<FieldDTO> dtos);
}
