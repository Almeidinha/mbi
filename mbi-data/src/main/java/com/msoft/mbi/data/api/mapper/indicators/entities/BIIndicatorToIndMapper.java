package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.dtos.indicators.entities.BIIndDTO;
import com.msoft.mbi.data.api.mapper.indicators.FieldToBIAnalysisFieldMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = FieldToBIAnalysisFieldMapper.class)
public interface BIIndicatorToIndMapper {

    BIIndicatorToIndMapper BI_IND_LOGIC_MAPPER = Mappers.getMapper(BIIndicatorToIndMapper.class);


    @Mappings({
            @Mapping(target = "id", source = "code"),
            @Mapping(target = "biAreaByArea.id", source = "areaCode"),
            @Mapping(target = "connectionId", source = "tenantId"),
            @Mapping(target = "biSearchClause.sqlText", source = "searchClause"),
            @Mapping(target = "biFromClause.sqlText", source = "fromClause"),
            @Mapping(target = "biWhereClause.sqlText", source = "whereClause"),
            @Mapping(target = "biAnalysisFields", source = "fields"),
            @Mapping(target = "biDimensionFilter.sqlText", source = "dimensionFilters"),
            @Mapping(target = "biIndMetricFilter.sqlText", source = "metricFilters"),
            @Mapping(target = "biIndSqlMetricFilter.sqlText", source = "metricSqlFilters"),
    })
    BIIndDTO indicatorToDto(Indicator indicator) throws BIException;

    @Mappings({
            @Mapping(target = "code", source = "id"),
            @Mapping(target = "areaCode", source = "biAreaByArea.id"),
            @Mapping(target = "tenantId", source = "connectionId"),
            @Mapping(target = "searchClause", source = "biSearchClause.sqlText"),
            @Mapping(target = "fromClause", source = "biFromClause.sqlText"),
            @Mapping(target = "whereClause", source = "biWhereClause.sqlText"),
            @Mapping(target = "fields", source = "biAnalysisFields"),
            @Mapping(target = "dimensionFilters", source = "biDimensionFilter.sqlText"),
            @Mapping(target = "metricFilters", source = "biIndMetricFilter.sqlText"),
            @Mapping(target = "metricSqlFilters", source = "biIndSqlMetricFilter.sqlText"),
    })
    Indicator dtoToIndicator(BIIndDTO dto) throws BIException;


}