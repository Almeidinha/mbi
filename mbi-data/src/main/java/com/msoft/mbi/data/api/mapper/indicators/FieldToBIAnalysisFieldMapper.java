package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.List;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIAnalysisFieldDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;


@Qualifier
@Mapper(componentModel = "spring")
public interface FieldToBIAnalysisFieldMapper {
    FieldToBIAnalysisFieldMapper FIELD_TO_BI_ANALYSIS_FIELD_MAPPER = Mappers.getMapper(FieldToBIAnalysisFieldMapper.class);


    @Mappings({
            @Mapping(target = "vertical", source = "verticalAnalysisType"),
            @Mapping(target = "horizontal", source = "horizontalAnalysisType"),
            @Mapping(target = "filterSequence", source = "drillDownSequence"),
            @Mapping(target = "fieldOrder", source = "order"),
            @Mapping(target = "direction", source = "orderDirection"),
            @Mapping(target = "decimalPositions", source = "numDecimalPositions"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "fieldTotalization", source = "totalizingField"),
            @Mapping(target = "lineFieldTotalization", source = "sumLine"),
            @Mapping(target = "accumulatedLineField", source = "horizontalParticipationAccumulated"),

    })
    BIAnalysisFieldDTO fieldToDto(Field field) throws BIException;

    @Mappings({
            @Mapping(target = "verticalAnalysisType", source = "vertical"),
            @Mapping(target = "horizontalAnalysisType", source = "horizontal"),
            @Mapping(target = "drillDownSequence", source = "filterSequence"),
            @Mapping(target = "order", source = "fieldOrder"),
            @Mapping(target = "orderDirection", source = "direction"),
            @Mapping(target = "numDecimalPositions", source = "decimalPositions"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", source = "fieldTotalization"),
            @Mapping(target = "sumLine", source = "lineFieldTotalization"),
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
            @Mapping(target = "decimalPositions", source = "numDecimalPositions"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "fieldTotalization", source = "totalizingField"),
            @Mapping(target = "lineFieldTotalization", source = "sumLine"),
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
            @Mapping(target = "numDecimalPositions", source = "decimalPositions"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", source = "fieldTotalization"),
            @Mapping(target = "sumLine", source = "lineFieldTotalization"),
            @Mapping(target = "horizontalParticipationAccumulated", source = "accumulatedLineField"),
            @Mapping(target = "verticalAnalysis", expression = "java(!dto.getVertical().equals(\"N\"))"),
            @Mapping(target = "horizontalAnalysis", expression = "java(!dto.getHorizontal().equals(\"N\"))"),
    })
    List<Field> setDTOToEntity(List<BIAnalysisFieldDTO> dtos);
}
