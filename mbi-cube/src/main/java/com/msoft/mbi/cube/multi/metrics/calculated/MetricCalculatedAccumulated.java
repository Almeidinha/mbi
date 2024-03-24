package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricValueUse;


public class MetricCalculatedAccumulated extends MetricCalculated {


    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        MetricCalculatedAcumuladoMetaData metaData = (MetricCalculatedAcumuladoMetaData) this.getMetaData();
        Calculation calculation = metaData.createCalculo();

        try {
            String title = calculation.getVariables().get(MetricCalculatedAcumuladoMetaData.COLUNA_AV_VARIABLE);
            Metric referenceMetric = metricLine.getMetrics().get(title);
            Double currentValue = referenceMetric.getValue(metricsMap, metricLine, metricLineAnterior);
            calculation.setVariableValue(MetricCalculatedAcumuladoMetaData.COLUNA_AV_VARIABLE, (currentValue != null ? currentValue : 0.0));

            Double previousValue = 0.0;

            if (metaData instanceof MetricCalculatedAcumuladoParticipacaoAVMetaData || metaData instanceof MetricCalculatedAcumuladoValorAVMetaData) {
                if (metricLineAnterior != null) {
                    Metric accumulatedValue = metricLineAnterior.getMetrics().get(metaData.getTitle());
                    previousValue = accumulatedValue.getValue(metricsMap, metricLineAnterior, null);

                    Dimension currentDimension = metaData.getParticipationAnalysisType().getDimensaoNivelAcima(metricLine.getDimensionLine());
                    Dimension previousDimension = metaData.getParticipationAnalysisType().getDimensaoNivelAcima(metricLineAnterior.getDimensionLine());

                    if (currentDimension != previousDimension && !currentDimension.getKeyValue().equals(previousDimension.getKeyValue())) {
                        previousValue = 0.0;
                    }
                }
            }

            if (metaData instanceof MetricCalculatedAcumuladoParticipacaoAHMetaData) {
                Dimension dimensionAnterior = this.getPreviousDimension(metricLine.getDimensionColumn(), metaData);

                if (dimensionAnterior != null) {
                    Metric expressionValue = metricLine.getMetrics().get(metaData.getTitle());
                    previousValue = expressionValue.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), dimensionAnterior);
                }
            }

            calculation.setVariableValue(MetricCalculatedAcumuladoMetaData.COLUNA_VALOR_ANTERIOR_VARIABLE, previousValue);
            return calculation.calculateValue();
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
        }
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricValueUse calculateLevel) {
        return calculate(metricsMap, metricLine, metricLineAnterior);
    }

    private Dimension getPreviousDimension(Dimension currentDimension, MetricCalculatedAcumuladoMetaData metaData) {
        return currentDimension.getPreviousDimension(metaData.getParticipationAnalysisType().getDimensaoNivelAcima(currentDimension));
    }

    @Override
    public Double getValue(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        if (this.aggregator.getAggregatorValue() == null) {
            this.aggregator.setValue(this.calculate(metricsMap, metricLine, metricLineAnterior));
        }
        return this.aggregator.getAggregatorValue();
    }

}
