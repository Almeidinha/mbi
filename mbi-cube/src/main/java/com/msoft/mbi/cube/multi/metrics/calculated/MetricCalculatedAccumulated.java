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
        MetricCalculatedAccMetaData metaData = (MetricCalculatedAccMetaData) this.getMetaData();
        Calculation calculation = metaData.createCalculation();

        try {
            String title = calculation.getVariables().get(MetricCalculatedAccMetaData.AV_COLUMN_VARIABLE);
            Metric referenceMetric = metricLine.getMetrics().get(title);
            Double currentValue = referenceMetric.getValue(metricsMap, metricLine, metricLineAnterior);
            calculation.setVariableValue(MetricCalculatedAccMetaData.AV_COLUMN_VARIABLE, (currentValue != null ? currentValue : 0.0));

            Double previousValue = 0.0;

            if (metaData instanceof MetricCalculatedAccParticipationAVMetaData || metaData instanceof MetricCalculatedAccValorAVMetaData) {
                if (metricLineAnterior != null) {
                    Metric accumulatedValue = metricLineAnterior.getMetrics().get(metaData.getTitle());
                    previousValue = accumulatedValue.getValue(metricsMap, metricLineAnterior, null);

                    Dimension currentDimension = metaData.getParticipationAnalysisType().getUpperLevelDimension(metricLine.getDimensionLine());
                    Dimension previousDimension = metaData.getParticipationAnalysisType().getUpperLevelDimension(metricLineAnterior.getDimensionLine());

                    if (currentDimension != previousDimension && !currentDimension.getKeyValue().equals(previousDimension.getKeyValue())) {
                        previousValue = 0.0;
                    }
                }
            }

            if (metaData instanceof MetricCalculatedAccParticipationAHMetaData) {
                Dimension dimensionAnterior = this.getPreviousDimension(metricLine.getDimensionColumn(), metaData);

                if (dimensionAnterior != null) {
                    Metric expressionValue = metricLine.getMetrics().get(metaData.getTitle());
                    previousValue = expressionValue.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), dimensionAnterior);
                }
            }

            calculation.setVariableValue(MetricCalculatedAccMetaData.PREVIOUS_VALUE_COLUMN_VARIABLE, previousValue);
            return calculation.calculateValue();
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
        }
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricValueUse calculateLevel) {
        return calculate(metricsMap, metricLine, metricLineAnterior);
    }

    private Dimension getPreviousDimension(Dimension currentDimension, MetricCalculatedAccMetaData metaData) {
        return currentDimension.getPreviousDimension(metaData.getParticipationAnalysisType().getUpperLevelDimension(currentDimension));
    }

    @Override
    public Double getValue(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        if (this.aggregator.getAggregatorValue() == null) {
            this.aggregator.setValue(this.calculate(metricsMap, metricLine, metricLineAnterior));
        }
        return this.aggregator.getAggregatorValue();
    }

}
