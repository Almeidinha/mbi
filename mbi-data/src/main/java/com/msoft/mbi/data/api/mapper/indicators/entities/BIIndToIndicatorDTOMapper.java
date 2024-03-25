package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.model.BIIndEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
    })
    List<BIIndEntity> setDTOToEntityList(List<IndicatorDTO> dtos);
}
