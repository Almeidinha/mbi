package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.dtos.indicators.BIIndLogicDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = BIAnalysisFieldToFieldMapper.class)
public interface BIIndLogicToIndMapper {

    BIIndLogicToIndMapper BI_IND_LOGIC_MAPPER = Mappers.getMapper(BIIndLogicToIndMapper.class);


    @Mappings({
            @Mapping(target = "connectionId", source = "tenantId"),
            @Mapping(target = "biSearchClause.sqlText", source = "searchClause"),
            @Mapping(target = "biFromClause.sqlText", source = "fromClause"),
            @Mapping(target = "biWhereClause.sqlText", source = "whereClause"),
            @Mapping(target = "biAnalysisFields", source = "fields"),
            @Mapping(target = "biDimensionFilter.sqlText", source = "dimensionFilters"),
            @Mapping(target = "biIndMetricFilter.sqlText", source = "metricFilters"),
            @Mapping(target = "biIndSqlMetricFilter.sqlText", source = "metricSqlFilters"),
    })
    BIIndLogicDTO indicatorToDto(Indicator indicator) throws BIException;

    @Mappings({
            @Mapping(target = "tenantId", source = "connectionId"),
            @Mapping(target = "searchClause", source = "biSearchClause.sqlText"),
            @Mapping(target = "fromClause", source = "biFromClause.sqlText"),
            @Mapping(target = "whereClause", source = "biWhereClause.sqlText"),
            @Mapping(target = "fields", source = "biAnalysisFields"),
            @Mapping(target = "dimensionFilters", source = "biDimensionFilter.sqlText"),
            @Mapping(target = "metricFilters", source = "biIndMetricFilter.sqlText"),
            @Mapping(target = "metricSqlFilters", source = "biIndSqlMetricFilter.sqlText"),
    })
    Indicator dtoToIndicator(BIIndLogicDTO dto) throws BIException;


}