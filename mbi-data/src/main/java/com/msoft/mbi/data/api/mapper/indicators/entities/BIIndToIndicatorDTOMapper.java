package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionDTO;
import com.msoft.mbi.model.BIDimMetricRestrictionEntity;
import com.msoft.mbi.model.BIIndEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = BIAnalysisFieldToFieldDTOMapper.class)
public interface BIIndToIndicatorDTOMapper {

    BIIndToIndicatorDTOMapper BI_IND_TO_INDICATOR_MAPPER = Mappers.getMapper(BIIndToIndicatorDTOMapper.class);


    @Mappings({
            @Mapping(target = "code", source = "id"),
            @Mapping(target = "areaCode", source = "biAreaByArea.id"),
            @Mapping(target = "companyId", source = "companyIdByCompany.id"),
            @Mapping(target = "tenantId", source = "connectionId"),
            @Mapping(target = "searchClause", source = "biSearchClause.sqlText"),
            @Mapping(target = "fromClause", source = "biFromClause.sqlText"),
            @Mapping(target = "whereClause", source = "biWhereClause.sqlText"),
            @Mapping(target = "fields", source = "biAnalysisFields"),
            @Mapping(target = "dimensionFilters", source = "biDimensionFilter.sqlText"),
            @Mapping(target = "metricFilters", source = "biIndMetricFilter.sqlText"),
            @Mapping(target = "metricSqlFilters", source = "biIndSqlMetricFilter.sqlText"),
            @Mapping(target = "metricDimensionRestrictions", source  = "biDimMetricRestrictions", qualifiedByName = "getMetricDimRestrictionsDTO"),
            @Mapping(target = "multidimensional", expression = "java(entity.getTableType().equals(2))"),
    })
    IndicatorDTO biEntityToDTO(BIIndEntity entity);

    @Mappings({
            @Mapping(target = "id", source = "code"),
            @Mapping(target = "biAreaByArea.id", source = "areaCode"),
            @Mapping(target = "companyIdByCompany.id", source = "companyId"),
            @Mapping(target = "connectionId", source = "tenantId"),
            @Mapping(target = "biSearchClause.sqlText", source = "searchClause"),
            @Mapping(target = "biFromClause.sqlText", source = "fromClause"),
            @Mapping(target = "biWhereClause.sqlText", source = "whereClause"),
            @Mapping(target = "biAnalysisFields", source = "fields"),
            @Mapping(target = "biDimensionFilter.sqlText", source = "dimensionFilters"),
            @Mapping(target = "biIndMetricFilter.sqlText", source = "metricFilters"),
            @Mapping(target = "biIndSqlMetricFilter.sqlText", source = "metricSqlFilters"),
            @Mapping(target = "biDimMetricRestrictions", source  = "dto", qualifiedByName = "getMetricDimRestrictionEntities"),
            @Mapping(target = "tableType", expression = "java(dto.isMultidimensional() ? 2 : 1)"),
    })
    BIIndEntity dtoToEntity(IndicatorDTO dto);

    @Mappings({
            @Mapping(target = "code", source = "id"),
            @Mapping(target = "areaCode", source = "biAreaByArea.id"),
            @Mapping(target = "companyId", source = "companyIdByCompany.id"),
            @Mapping(target = "tenantId", source = "connectionId"),
            @Mapping(target = "searchClause", source = "biSearchClause.sqlText"),
            @Mapping(target = "fromClause", source = "biFromClause.sqlText"),
            @Mapping(target = "whereClause", source = "biWhereClause.sqlText"),
            @Mapping(target = "fields", source = "biAnalysisFields"),
            @Mapping(target = "dimensionFilters", source = "biDimensionFilter.sqlText"),
            @Mapping(target = "metricFilters", source = "biIndMetricFilter.sqlText"),
            @Mapping(target = "metricSqlFilters", source = "biIndSqlMetricFilter.sqlText"),
            @Mapping(target = "metricDimensionRestrictions", source  = "biDimMetricRestrictions", qualifiedByName = "getMetricDimRestrictionsDTO"),
            @Mapping(target = "multidimensional", expression = "java(entity.getTableType().equals(2))"),
    })
    List<IndicatorDTO> entityListToDTO(List<BIIndEntity> entities);

    @Mappings({
            @Mapping(target = "id", source = "code"),
            @Mapping(target = "biAreaByArea.id", source = "areaCode"),
            @Mapping(target = "companyIdByCompany.id", source = "companyId"),
            @Mapping(target = "connectionId", source = "tenantId"),
            @Mapping(target = "biSearchClause.sqlText", source = "searchClause"),
            @Mapping(target = "biFromClause.sqlText", source = "fromClause"),
            @Mapping(target = "biWhereClause.sqlText", source = "whereClause"),
            @Mapping(target = "biAnalysisFields", source = "fields"),
            @Mapping(target = "biDimensionFilter.sqlText", source = "dimensionFilters"),
            @Mapping(target = "biIndMetricFilter.sqlText", source = "metricFilters"),
            @Mapping(target = "biIndSqlMetricFilter.sqlText", source = "metricSqlFilters"),
            @Mapping(target = "biDimMetricRestrictions", source  = "dto", qualifiedByName = "getMetricDimRestrictionEntities"),
            @Mapping(target = "tableType", expression = "java(dto.isMultidimensional() ? 2 : 1)"),
    })
    List<BIIndEntity> setDTOToEntityList(List<IndicatorDTO> dtos);

    @Named("getMetricDimRestrictionsDTO")
    default List<MetricDimensionRestrictionDTO> getMetricDimRestrictionsDTO(List<BIDimMetricRestrictionEntity> restrictionEntities) {
        if (restrictionEntities == null || restrictionEntities.isEmpty()) {
            return List.of();
        }

        Map<Integer, List<Integer>> metricToDimensionMap = restrictionEntities.stream()
                .collect(Collectors.groupingBy(
                        BIDimMetricRestrictionEntity::getMetricId,
                        Collectors.mapping(BIDimMetricRestrictionEntity::getDimensionId, Collectors.toList())
                ));

        return metricToDimensionMap.entrySet().stream()
                .map(entry -> new MetricDimensionRestrictionDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Named("getMetricDimRestrictionEntities")
    default List<BIDimMetricRestrictionEntity> getMetricDimRestrictionEntities(IndicatorDTO dto) {
        if (dto.getMetricDimensionRestrictions() == null || dto.getMetricDimensionRestrictions().isEmpty()) {
            return List.of();
        }
        return dto.getMetricDimensionRestrictions().stream()
                .flatMap(b -> b.getDimensionIds().stream().map(dimensionId -> BIDimMetricRestrictionEntity
                        .builder()
                        .indicatorId(0)
                        .dimensionId(dimensionId)
                        .metricId(b.getMetricId()).build()))
                .toList();
    }

}
