package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricValueUse;


public class MetricCalculatedEvolutionAH extends MetricCalculated {


    private Dimension getPreviousDimension(Dimension currentDimension, MetricCalculatedAHEvolutionMetaData metaData) {
        return metaData.getEvolutionAnalysisType().getPreviousDimension(currentDimension);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricCalculatedAHEvolutionMetaData metaData = (MetricCalculatedAHEvolutionMetaData) this.getMetaData();
        Calculation calculation = metaData.createCalculo();
        Double result = null;
        try {
            String columnAHTitle = calculation.getVariables().get(MetricCalculatedAHEvolutionMetaData.AH_COLUMN_VARIABLE);

            Metric metricReference = metricLine.getMetrics().get(columnAHTitle);
            Double oValor = metricReference.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
            double currentValue = oValor != null ? oValor : 0;

            double valorAnterior;

            Dimension dimensionAnterior = this.getPreviousDimension(metricLine.getDimensionColumn(), metaData);

            Metric expressionValue = metricLine.getMetrics().get(columnAHTitle);
            oValor = expressionValue.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), dimensionAnterior);
            valorAnterior = oValor != null ? oValor : 0;

            if (currentValue != valorAnterior) {
                if (valorAnterior != 0) {

                    calculation.setVariableValue(MetricCalculatedAHEvolutionMetaData.AH_COLUMN_VARIABLE, currentValue);
                    calculation.setVariableValue(MetricCalculatedAHEvolutionMetaData.PREVIOUS_VALUE_COLUMN_VARIABLE, valorAnterior);
                    double valorCalculado = calculation.calculateValue();
                    if (valorAnterior < 0) {
                        valorCalculado = valorCalculado * (-1);
                    }
                    result = valorCalculado;
                } else {
                    if (currentValue > 0) {
                        result = 100.0;
                    } else if (currentValue < 0) {
                        result = (double) -100;
                    }
                }
            } else {
                result = (double) 0;
            }
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
        }
        return result;
    }

    @Override
    public Double getValue(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricValueUse calculateLevel) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior);
    }

}
