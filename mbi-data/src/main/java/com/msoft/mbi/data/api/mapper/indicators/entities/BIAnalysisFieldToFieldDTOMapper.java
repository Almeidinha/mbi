package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.FieldDTO;
import com.msoft.mbi.model.BIAnalysisFieldEntity;
import com.msoft.mbi.model.BiAnalysisFieldId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BIAnalysisFieldToFieldDTOMapper {

    BIAnalysisFieldToFieldDTOMapper BI_ANALYSIS_FIELD_TO_FIELD_MAPPER = Mappers.getMapper(BIAnalysisFieldToFieldDTOMapper.class);

    @Mappings({
            @Mapping(target = "verticalAnalysisType", source = "vertical"),
            @Mapping(target = "horizontalAnalysisType", source = "horizontal"),
            @Mapping(target = "drillDownSequence", source = "filterSequence"),
            @Mapping(target = "order", source = "fieldOrder"),
            @Mapping(target = "orderDirection", source = "direction"),
            @Mapping(target = "numDecimalPositions", source = "decimalPositions"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", expression = "java(!entity.getFieldTotalization().equals(\"N\"))"),
            @Mapping(target = "sumLine", source = "lineFieldTotalization"),
            @Mapping(target = "accumulatedLine", source = "accumulatedLineField"),
            @Mapping(target = "indicatorId", source = "id.indicatorId"),
            @Mapping(target = "fieldId", source = "id.fieldId"),
            @Mapping(target = "mediaLine", source = "usesMediaLine"),
            @Mapping(target = "applyTotalizationExpression", expression = "java(entity.getFieldTotalization().equals(\"E\"))"),
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
            @Mapping(target = "decimalPositions", source = "numDecimalPositions"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "fieldTotalization", source = "dto", qualifiedByName = "getFieldTotalization"),
            @Mapping(target = "id", source = "dto", qualifiedByName = "getBiAnalysisFieldId"),
            @Mapping(target = "lineFieldTotalization", source = "sumLine"),
            @Mapping(target = "usesMediaLine", source = "mediaLine"),
            @Mapping(target = "accumulatedLineField", source = "accumulatedLine"),

    })
    BIAnalysisFieldEntity dtoToEntity(FieldDTO dto);

    @Mappings({
            @Mapping(target = "verticalAnalysisType", source = "vertical"),
            @Mapping(target = "horizontalAnalysisType", source = "horizontal"),
            @Mapping(target = "drillDownSequence", source = "filterSequence"),
            @Mapping(target = "order", source = "fieldOrder"),
            @Mapping(target = "orderDirection", source = "direction"),
            @Mapping(target = "numDecimalPositions", source = "decimalPositions"),
            @Mapping(target = "displayLocation", source = "localApres"),
            @Mapping(target = "totalizingField", expression = "java(!entity.getFieldTotalization().equals(\"N\"))"),
            @Mapping(target = "sumLine", source = "lineFieldTotalization"),
            @Mapping(target = "accumulatedLine", source = "accumulatedLineField"),
            @Mapping(target = "indicatorId", source = "id.indicatorId"),
            @Mapping(target = "fieldId", source = "id.fieldId"),
            @Mapping(target = "mediaLine", source = "usesMediaLine"),
            @Mapping(target = "applyTotalizationExpression", expression = "java(entity.getFieldTotalization().equals(\"E\"))"),
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
            @Mapping(target = "decimalPositions", source = "numDecimalPositions"),
            @Mapping(target = "localApres", source = "displayLocation"),
            @Mapping(target = "id", source = "dto", qualifiedByName = "getBiAnalysisFieldId"),
            @Mapping(target = "fieldTotalization", source = "dto", qualifiedByName = "getFieldTotalization"),
            @Mapping(target = "lineFieldTotalization", source = "sumLine"),
            @Mapping(target = "usesMediaLine", source = "mediaLine"),
            @Mapping(target = "accumulatedLineField", source = "accumulatedLine"),

    })
    List<BIAnalysisFieldEntity> setDTOToEntity(List<FieldDTO> dtos);


    @Named("getFieldTotalization")
    default String getTotalizingField(FieldDTO dto) {
        if (dto.isApplyTotalizationExpression()) {
            return "E";
        }
        return dto.isTotalizingField() ? "S" : "N";
    }

    @Named("getBiAnalysisFieldId")
    default BiAnalysisFieldId getBiAnalysisFieldId(FieldDTO dto) {
        return BiAnalysisFieldId.builder()
                .fieldId(dto.getFieldId())
                .indicatorId(dto.getIndicatorId())
                .build();
    }

}
