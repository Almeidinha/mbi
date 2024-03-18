package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.inputs.FilterBuilderInput;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringTokenizer;

@NoArgsConstructor
public class FilterBuilder {

    public void addFilter(Filters filters, Field field, String operator, String value, String link, String connector)
            throws BIException {
        if (field.getFieldType().equals("D")) {
            String l = "";
            if (link.length() > 1)
                l = link.substring(2);
            if (filters.getDimensionFilter() == null) {
                filters.setDimensionFilter(FilterFactory.createDimensionFilter(field, operator, value));
            } else {
                this.addFilter(filters.getDimensionFilter(), field, operator, value, l, connector);
            }
        } else {
            if ("4".equals(link)) {
                if (filters.getMetricSqlFilter() == null) {
                    MetricSqlFilter metricSqlFilter = new MetricSqlFilter();
                    metricSqlFilter.add(FilterFactory.createMetricFilter(field, operator, value));
                    filters.setMetricSqlFilter(metricSqlFilter);
                } else {
                    this.addFilter(filters.getMetricSqlFilter(), field, operator, value);
                }
            } else {
                if (filters.getMetricFilters() == null) {
                    MetricFilters metricFilters = new MetricFilters();
                    metricFilters.add(FilterFactory.createMetricFilter(field, operator, value));
                    filters.setMetricFilters(metricFilters);
                } else {
                    this.addFilter(filters.getMetricFilters(), field, operator, value);
                }
            }
        }
    }

    private void addFilter(MetricSqlFilter metricSqlFilter, Field field, String operator, String value)
            throws BIException {
        MetricFilter filterJDBC = new MetricFilterJDBC();
        filterJDBC.setCondition(field, operator, value);
        metricSqlFilter.add(filterJDBC);
    }

    public void addFilter(MetricFilters metricFilters, Field field, String operator, String value)
            throws BIException {
        MetricFilter filterJDBC = new MetricFilterJDBC();
        filterJDBC.setCondition(field, operator, value);
        metricFilters.add(filterJDBC);
    }

    public void addFilter(DimensionFilter dimensionFilter, Field field, String operator, String value, String link,
                          String connector) throws BIException {
        StringTokenizer strTok = new StringTokenizer(link, "-");
        DimensionFilter parentFilter = null;
        while (strTok.hasMoreElements()) {
            String temp = (String) strTok.nextElement();
            if (!temp.equals("0")) {
                parentFilter = dimensionFilter;
                dimensionFilter = dimensionFilter.getDimensionFilter(Integer.parseInt(temp) - 1);
            }
        }
        createDimensionFilter(dimensionFilter, parentFilter, field, operator, value, connector);
    }

    public void addFilter(FiltersFunction filtersFunction, String operator, String valor, Field field, int filterType) {
        Operator op = new Operator(operator);

        valor = valor.replace('.', 'X');
        valor = valor.replaceAll("X", "");

        valor = valor.replace(',', '.');

        if (filterType == FilterFunction.SEQUENCE_FILTER) {
            FilterSequence filterSequence = new FilterSequence(op, valor);
            filtersFunction.removeFilterSequence();
            filtersFunction.addFilter(filterSequence);
        } else if (filterType == FilterFunction.ACCUMULATED_FILTER) {
            FilterAccumulated filterAccumulated = new FilterAccumulated(op, valor, field);
            filtersFunction.removeFilterAccumulated();
            filtersFunction.addFilter(filterAccumulated);
        }
    }

    private void createDimensionFilter(DimensionFilter dimensionFilter, DimensionFilter parentFilter, Field field, String operator,
                                       String valor, String connector) throws BIException {
        if ((dimensionFilter.getCondition() == null && !dimensionFilter.isMacro())
                && (dimensionFilter.getConnector() == null || connector.equals(dimensionFilter.getConnector()))) {
            if (dimensionFilter.hasFilters() > 0) {
                DimensionFilter novo = FilterFactory.createDimensionFilter(field, operator, valor);
                novo.setDrillDown(false);
                dimensionFilter.addDimensionFilter(novo);
                dimensionFilter.setConnector(connector);
            } else {
                FilterFactory.createDimensionFilter(field, operator, valor);
            }
        } else {
            DimensionFilter newFilter = FilterFactory.createDimensionFilter(field, operator, valor);
            if (parentFilter != null && parentFilter.isMacro()) {
                DimensionFilter copy = (DimensionFilter) parentFilter.clone();
                parentFilter.removeAll();
                parentFilter.addDimensionFilter(copy);
                parentFilter.addDimensionFilter(newFilter);
                parentFilter.setConnector(connector);
            } else if (parentFilter != null && parentFilter.getConnector().equals(connector)) {
                parentFilter.addDimensionFilter(newFilter);
            } else {
                DimensionFilter copy = (DimensionFilter) dimensionFilter.clone();
                dimensionFilter.removeAll();
                dimensionFilter.addDimensionFilter(copy);

                dimensionFilter.addDimensionFilter(newFilter);
                dimensionFilter.setConnector(connector);
            }
        }
    }

    public void removeFilter(Filters filters, String link) throws BIException {
        if (link.charAt(0) == '1')
            this.removeFilter(filters.getDimensionFilter(), link.substring(2));
        else
            this.removeFilter(filters.getMetricFilters(), link.substring(1));
    }

    private void removeFilter(MetricFilters metricFilters, String link) {
        metricFilters.remove(link);
    }

    private void removeFilter(DimensionFilter filtroDimensao, String link) throws BIException {
        StringTokenizer strTok = new StringTokenizer(link, "-");
        while (strTok.countTokens() > 1) {
            filtroDimensao = filtroDimensao.getDimensionFilter(Integer.parseInt(strTok.nextToken()) - 1);
        }
        String lnk = "";
        if (strTok.hasMoreElements()) {
            lnk = (String) strTok.nextElement();
        }
        removeDimensionFilter(filtroDimensao, lnk);
    }
    
    private void removeDimensionFilter(DimensionFilter dimensionFilter, String link) {
        if (!link.isEmpty()) {
            dimensionFilter.remove(Integer.parseInt(link));
        }
    }

    public void removeFilterByLink(Filters filters, FiltersFunction filtersFunction, Field field, FilterBuilderInput input) throws BIException {
        String link = input.getLink();
        if (link.charAt(0) == '1') {
            if (filters.getDimensionFilter() != null) {
                DimensionFilter chieldFilter = filters.getDimensionFilter();
                DimensionFilter parentFilter = null;
                if (link.length() > 1 && !link.substring(2).equals("0")) {
                    link = link.substring(2);
                    StringTokenizer strTok = new StringTokenizer(link, "-");
                    String nxt;
                    while (strTok.hasMoreElements()) {
                        nxt = (String) strTok.nextElement();
                        parentFilter = chieldFilter;
                        chieldFilter = chieldFilter.getDimensionFilter(Integer.parseInt(nxt) - 1);
                    }
                }
                filters.removeDimensionFilter(parentFilter, chieldFilter, true);
            }
        } else {
            if (link.charAt(0) == '3') {
                if (field.getName().equalsIgnoreCase("#$sequencia$!")) {
                    filtersFunction.removeFilter(filtersFunction.getFilterSequence());
                } else {
                    filtersFunction.removeFilter(filtersFunction.getFilterAccumulated());
                }
            } else {
                String value = input.getValue();
                String symbol = input.getOperator();
                if (link.charAt(0) == '4') {
                    filters.removeApplicableMetricSqlFilter(value, symbol, String.valueOf(field.getFieldId()));
                } else {
                    filters.removeMetricFilter(value, symbol, String.valueOf(field.getFieldId()));
                }
            }
        }
    }
}
