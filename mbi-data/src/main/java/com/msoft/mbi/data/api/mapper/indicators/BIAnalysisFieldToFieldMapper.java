package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.dtos.indicators.BIAnalysisFieldDTO;

import java.util.List;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;


@Qualifier
@Mapper(componentModel = "spring")
public interface BIAnalysisFieldToFieldMapper {
    BIAnalysisFieldToFieldMapper BI_ANALYSIS_FIELD_TO_FIELD_MAPPER = Mappers.getMapper(BIAnalysisFieldToFieldMapper.class);


    @Mappings({
            @Mapping(target = "vertical", source = "verticalAnalysisType"),
            @Mapping(target = "horizontal", source = "horizontalAnalysisType"),
            @Mapping(target = "filterSequence", source = "drillDownSequence"),
            @Mapping(target = "fieldOrder", source = "order"),
            @Mapping(target = "direction", source = "orderDirection"),
            @Mapping(target = "decimalPosition", source = "numDecimalPositions"),
            @Mapping(target = "fieldTotalization", source = "partialTotalization"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "lineFieldTotalization", source = "totalizingField"),
            @Mapping(target = "accumulatedLineField", source = "horizontalParticipationAccumulated"),

    })
    BIAnalysisFieldDTO fieldToDto(Field field) throws BIException;

    @Mappings({
            @Mapping(target = "verticalAnalysisType", source = "vertical"),
            @Mapping(target = "horizontalAnalysisType", source = "horizontal"),
            @Mapping(target = "drillDownSequence", source = "filterSequence"),
            @Mapping(target = "order", source = "fieldOrder"),
            @Mapping(target = "orderDirection", source = "direction"),
            @Mapping(target = "numDecimalPositions", source = "decimalPosition"),
            @Mapping(target = "partialTotalization", source = "fieldTotalization"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", source = "lineFieldTotalization"),
            @Mapping(target = "horizontalParticipationAccumulated", source = "accumulatedLineField"),
            @Mapping(target = "verticalAnalysis", expression = "java(!dto.getVertical().equals(\"N\"))"),
            @Mapping(target = "horizontalAnalysis", expression = "java(!dto.getHorizontal().equals(\"N\"))"),
    })
    Field dtoToField(BIAnalysisFieldDTO dto) throws BIException;

    @Named("fieldToBIAnalysisFieldDTOList")
    @Mappings({
            @Mapping(target = "vertical", source = "verticalAnalysisType"),
            @Mapping(target = "horizontal", source = "horizontalAnalysisType"),
            @Mapping(target = "filterSequence", source = "drillDownSequence"),
            @Mapping(target = "fieldOrder", source = "order"),
            @Mapping(target = "direction", source = "orderDirection"),
            @Mapping(target = "numDecimalPositions", source = "decimalPosition"),
            @Mapping(target = "fieldTotalization", source = "partialTotalization"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "lineFieldTotalization", source = "totalizingField"),
            @Mapping(target = "accumulatedLineField", source = "horizontalParticipationAccumulated"),
    })
    List<BIAnalysisFieldDTO> setEntityToDTO(List<Field> entities);

    @Named("biAnalysisFieldDTOToFieldList")
    @Mappings({
            @Mapping(target = "verticalAnalysisType", source = "vertical"),
            @Mapping(target = "horizontalAnalysisType", source = "horizontal"),
            @Mapping(target = "drillDownSequence", source = "filterSequence"),
            @Mapping(target = "order", source = "fieldOrder"),
            @Mapping(target = "orderDirection", source = "direction"),
            @Mapping(target = "decimalPosition", source = "numDecimalPositions"),
            @Mapping(target = "partialTotalization", source = "fieldTotalization"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", source = "lineFieldTotalization"),
            @Mapping(target = "horizontalParticipationAccumulated", source = "accumulatedLineField"),
            @Mapping(target = "verticalAnalysis", expression = "java(!dto.getVertical().equals(\"N\"))"),
            @Mapping(target = "horizontalAnalysis", expression = "java(!dto.getHorizontal().equals(\"N\"))"),
    })
    List<Field> setDTOToEntity(List<BIAnalysisFieldDTO> dtos);
}
