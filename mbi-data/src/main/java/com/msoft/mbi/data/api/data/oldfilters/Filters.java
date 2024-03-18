package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
public class Filters {

    private DimensionFilter dimensionFilter;
    private MetricFilters metricFilters;
    private MetricSqlFilter metricSqlFilter;

    public void setMetricSqlFilter(List<MetricFilter> metricSqlFilter) { // TODO TEST this, does in not work like metricFilters?
        this.metricSqlFilter = new MetricSqlFilter();
        this.metricSqlFilter.addAll(metricSqlFilter);
    }

    public Integer applyMetricFilterValuesInMetric(PreparedStatement stmt, int position) throws BIException {
        if (this.metricSqlFilter != null) {
            return this.metricSqlFilter.applyValues(stmt, position);
        }
        return position;
    }


    public int applyValuesInDimension(PreparedStatement stmt, int position) throws SQLException, BIException {
        if (this.dimensionFilter != null) {
            position = (Integer) this.dimensionFilter.applyValues(stmt, position);
        }
        return position;
    }

    public int applyValuesInMetric(PreparedStatement stmt, int position) throws BIException {
        if (this.metricFilters != null) {
            position = this.metricFilters.applyValues(stmt, position);
        }
        return position;
    }

    @Override
    public Filters clone() throws CloneNotSupportedException {
        Filters filters = (Filters) super.clone();
        if (this.dimensionFilter != null) {
            filters.setDimensionFilter((DimensionFilter) this.dimensionFilter.clone());
        }
        if (this.metricFilters != null) {
            filters.setMetricFilters((MetricFilters) this.metricFilters.clone());
        }
        if (this.metricSqlFilter != null) {
            filters.setMetricSqlFilter(
                    Stream.of(this.metricSqlFilter.clone())
                            .map(MetricFilter.class::cast)
                            .collect(Collectors.toList())
            );
        }
        return filters;
    }

    public void removeDimensionFilter(DimensionFilter parentFilter, DimensionFilter childFilter, boolean remove) throws BIException {
        if (Optional.ofNullable(parentFilter).isEmpty() && (childFilter.isDrillDown() || remove)) {
            this.dimensionFilter = null;
            return;
        }

        if (Optional.ofNullable(parentFilter).isEmpty()) {
            return;
        }

        if (!parentFilter.isMacro()) {
            int indice = -1;
            List<DimensionFilter> filters = parentFilter.getFilters();
            for (int i = 0; i < filters.size(); i++) {
                if (filters.get(i) == childFilter) {
                    indice = i;
                    break;
                }
            }

            if (indice != -1 && (childFilter.isDrillDown() || remove)) {
                parentFilter.remove(indice);
            }
        } else {
            this.dimensionFilter = null;
        }

        if (parentFilter.hasFilters() == 1) {
            DimensionFilter firstFilter = parentFilter.getDimensionFilter(0);
            parentFilter.setCondition(firstFilter.getCondition());
            parentFilter.setConnector(firstFilter.getConnector());
            parentFilter.setFilters(firstFilter.getFilters());
            parentFilter.setMacro(firstFilter.getMacro(), firstFilter.getMacroField());
        }
    }

    public void removeMetricFilter(String value, String operator, String fieldCode) throws BIException {
        List<MetricFilter> metricFilters = this.getMetricFilters();
        this.removeMetricFilter(value, fieldCode, metricFilters);
    }

    public void removeApplicableMetricSqlFilter(String value, String operator, String fieldCode) throws BIException {
        List<MetricFilter> metricSqlFilter = this.getMetricSqlFilter();
        this.removeMetricFilter(value, fieldCode, metricSqlFilter);
    }

    private void removeMetricFilter(String value, String fieldCode, List<MetricFilter> metricFilters) throws BIException {
        if (metricFilters != null) {
            Iterator<MetricFilter> iterator = metricFilters.iterator();
            while (iterator.hasNext()) {
                MetricFilter metricFilter = iterator.next();
                if (metricFilter.getFormattedValue().equals(value) && metricFilter.getField().getCode() == Integer.parseInt(fieldCode)) {
                    iterator.remove();
                }
            }
        }
    }

    public void addFilter(Field field, String operator, String value) throws BIException {
        if (field.getFieldType().equals("D")) {
            DimensionFilter dimensionFilter = this.getDimensionFilter();
            if (dimensionFilter == null) {
                DimensionFilter novo = FilterFactory.createDimensionFilter(field, operator, value);
                this.setDimensionFilter(novo);
                novo.setDrillDown(true);
            } else {
                if (dimensionFilter.getCondition() == null && dimensionFilter.getConnector().equals("AND") && !dimensionFilter.isMacro()) {
                    DimensionFilter novo = FilterFactory.createDimensionFilter(field, operator, value);
                    dimensionFilter.addDimensionFilter(novo);
                    novo.setDrillDown(true);
                } else {
                    DimensionFilter fAux = (DimensionFilter) dimensionFilter.clone();
                    DimensionFilter novo = FilterFactory.createDimensionFilter(field, operator, value);
                    dimensionFilter.removeAll();
                    dimensionFilter.addDimensionFilter(fAux);
                    dimensionFilter.addDimensionFilter(novo);
                    dimensionFilter.setConnector("AND");
                    novo.setDrillDown(true);
                }
            }
        } else {
            if (this.getMetricFilters() == null) {
                MetricFilters fm = new MetricFilters();
                this.setMetricFilters(fm);
            }
            this.getMetricFilters().add(FilterFactory.createMetricFilter(field, operator, value));
        }
    }
}
