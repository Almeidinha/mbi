package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricValueUse;
import com.msoft.mbi.cube.multi.metrics.MetricValueUseLine;


public class MetricCalculated extends Metric {


    @Override
    public MetricCalculatedMetaData getMetaData() {
        return (MetricCalculatedMetaData) super.getMetaData();
    }

    @Override
    public Double getValue(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.aggregator.getAggregatorValue();
    }

    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricValueUse calculateLevel) {
        MetricCalculatedMetaData metaData = this.getMetaData();
        Calculation calculation = metaData.createCalculation();

        try {
            for (String variable : calculation.getVariables().values()) {
                Metric expression = findMetric(metricLine, variable);
                Double value = calculateLevel.calculateValue(expression, metricLine, metricsMap);
                calculation.setVariableValue(variable, value != null ? value : 0);
            }
            return calculation.calculateValue();
        } catch (Exception ex) {
            throw new CubeMathParserException("Unable to perform calculation for column " + metaData.getTitle() + ".", ex);
        }
    }

    private Metric findMetric(MetricLine metricLine, String variable) {
        Metric expression = metricLine.getMetrics().get(variable);
        if (expression == null) {
            expression = metricLine.getMetrics().get(removeBarVariable(variable));
        }
        return expression;
    }

    public String removeBarVariable(String variable) {
        return variable.replaceAll("\\\\", "");
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior, MetricValueUseLine.getInstance());
    }

    public void populateNewValue(Double valor) {
        this.getMetaData().getAggregationApplyOrder().populateNewValue(this, valor);
    }

    public void aggregateValue(Double valor) {
        this.aggregator.aggregatorValue(valor);
    }

    public void setValue(Double valor) {
        this.aggregator.setValue(valor);
    }

}
