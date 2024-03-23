package com.msoft.mbi.cube.multi.resumeFunctions;

import com.msoft.mbi.cube.multi.metrics.MetricValueUseTotal;

public class MetricFiltersAccumulatedValue extends MetricFilters {

    public MetricFiltersAccumulatedValue(String expression) {
        super(expression, MetricValueUseTotal.getInstance());
        for (String variable : this.filterExpression.getVariables().keySet()) {
            String tituloCampo = this.filterExpression.getVariables().get(variable);
            tituloCampo = tituloCampo.substring(5, tituloCampo.length() - 1);
            this.filterExpression.putVariable(variable, tituloCampo);
        }
    }
}
