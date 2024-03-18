package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarLinhaMetrica;


public class MetricCalculated extends Metric {


    @Override
    public MetricCalculatedMetaData getMetaData() {
        return (MetricCalculatedMetaData) super.getMetaData();
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.aggregator.getValorAgregado();
    }

    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar calculateLevel) {
        MetricCalculatedMetaData metaData = this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double result;
        try {
            for (String variable : calculo.getVariables().keySet()) {
                Metric expression = metricLine.getMetrics().get(calculo.getVariables().get(variable));
                if (expression == null) {
                    expression = metricLine.getMetrics().get(this.removeBarraVariable(calculo.getVariables().get(variable)));
                }

                Double valor = calculateLevel.calculaValor(expression, metricLine, metricsMap);
                calculo.setValorVariable(variable, (valor != null ? valor : 0));
            }
            result = calculo.calculaValor();
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
        }
        return result;
    }

    public String removeBarraVariable(String variable) {
        return variable.replaceAll("\\\\", "");
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior, MetricaValorUtilizarLinhaMetrica.getInstance());
    }

    public void populateNewValue(Double valor) {
        this.getMetaData().getAggregationApplyOrder().populateNewValue(this, valor);
    }

    public void aggregateValue(Double valor) {
        this.aggregator.agregaValor(valor);
    }

    public void setValue(Double valor) {
        this.aggregator.setValor(valor);
    }

}
