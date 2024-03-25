package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.*;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.data.inputs.FilterBuilderInput;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.api.mapper.filters.FiltersMapper;
import com.msoft.mbi.data.api.mapper.indicators.FieldMapper;
import com.msoft.mbi.data.api.mapper.indicators.IndicatorMapper;
import com.msoft.mbi.data.services.BIIndService;
import com.msoft.mbi.data.services.FiltersService;
import com.msoft.mbi.model.BIDimensionFilterEntity;
import com.msoft.mbi.model.BIIndEntity;
import com.msoft.mbi.model.BIMetricFilterEntity;
import com.msoft.mbi.model.BISqlMetricFiltersEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class FiltersServiceImpl implements FiltersService {

    private final BIIndService indService;
    private final IndicatorMapper indicatorMapper;
    private final FiltersMapper filtersMapper;
    private final FieldMapper fieldMapper;

    @Override
    public FiltersDTO getFiltersDTO(Integer id) throws BIException {

        IndicatorDTO dto = this.indService.getBIIndLogicDTO(id);
        Indicator ind = this.indicatorMapper.dtoToIndicator(dto);

        return this.filtersMapper.filterToDTO(ind.getFilters());
    }

    public FiltersDTO getFiltersDTOFromDTO(Integer id, IndicatorDTO dto) throws BIException {

        dto = Optional.ofNullable(dto).orElse(this.indService.getBIIndLogicDTO(id));
        Indicator ind = this.indicatorMapper.dtoToIndicator(dto);

        return this.filtersMapper.filterToDTO(ind.getFilters());
    }

    private void updateDimensionFilter(BIIndEntity entity, DimensionFilter dimensionFilter) {
        if (dimensionFilter == null) {
            entity.setBiDimensionFilter(null);
        } else {
            String trimmedSqlText = dimensionFilter.toString().trim();
            if (entity.getBiDimensionFilter() == null) {
                entity.setBiDimensionFilter(BIDimensionFilterEntity.builder().sqlText(trimmedSqlText).build());
            } else {
                entity.getBiDimensionFilter().setSqlText(trimmedSqlText);
            }
        }
    }

    private void updateMetricFilters(BIIndEntity entity, MetricFilters metricFilters) {
        if (metricFilters == null || metricFilters.isEmpty()) {
            entity.setBiIndMetricFilter(null);
        } else {
            String trimmedSqlText = metricFilters.toStringWithCode().trim();
            if (entity.getBiIndMetricFilter() == null) {
                entity.setBiIndMetricFilter(BIMetricFilterEntity.builder().sqlText(trimmedSqlText).build());
            } else {
                entity.getBiIndMetricFilter().setSqlText(trimmedSqlText);
            }
        }
    }

    private void updateMetricSqlFilter(BIIndEntity entity, MetricSqlFilter metricSqlFilter) {
        if (metricSqlFilter == null || metricSqlFilter.isEmpty()) {
            entity.setBiIndSqlMetricFilter(null);
        } else {
            String trimmedSqlText = metricSqlFilter.toStringWithCode().trim();
            if (entity.getBiIndSqlMetricFilter() == null) {
                entity.setBiIndSqlMetricFilter(BISqlMetricFiltersEntity.builder().sqlText(trimmedSqlText).build());
            } else {
                entity.getBiIndSqlMetricFilter().setSqlText(trimmedSqlText);
            }
        }
    }

    @Override
    public void updateIndFilters(Integer id, FiltersDTO dto) {
        Filters filters = this.filtersMapper.dtoToFilter(dto);

        if (filters != null) {
            BIIndEntity entity = this.indService.findById(id);

            updateDimensionFilter(entity, filters.getDimensionFilter());
            updateMetricFilters(entity, filters.getMetricFilters());
            updateMetricSqlFilter(entity, filters.getMetricSqlFilter());

            this.indService.save(entity);
        }
    }

    @Override
    public FiltersDTO buildFilters(FilterBuilderInput filterBuilderInput) throws BIException {
        FilterBuilder builder = new FilterBuilder();

        Filters filters = this.filtersMapper.dtoToFilter(filterBuilderInput.getFilters());

        builder.addFilter(
                filters,
                this.fieldMapper.dtoToField(filterBuilderInput.getField()),
                filterBuilderInput.getOperator(),
                filterBuilderInput.getValue(),
                filterBuilderInput.getLink(),
                filterBuilderInput.getConnector());

        return this.filtersMapper.filterToDTO(filters);
    }

    @Override
    public FiltersDTO removeFilter(FilterBuilderInput input) {
        FilterBuilder builder = new FilterBuilder();

        Filters filters = this.filtersMapper.dtoToFilter(input.getFilters());
        try {
            // TODO Criar mapper pra esse cara e passar pra função
            //FiltersFunction filtersFunction = input.getFiltersFunction();
            Field field = fieldMapper.dtoToField(input.getField());

            builder.removeFilterByLink(filters, null, field, input);
            return this.filtersMapper.filterToDTO(filters);
        } catch (BIException e) {
            log.error("Error while trying to remove a filter : " + e.getMessage());
        }

        return null;
    }

    @Override
    public FiltersDTO editFilter(FilterBuilderInput input) {
        FilterBuilder builder = new FilterBuilder();
        Filters filters = this.filtersMapper.dtoToFilter(input.getFilters());

        return this.filtersMapper.filterToDTO(filters);
    }


}
