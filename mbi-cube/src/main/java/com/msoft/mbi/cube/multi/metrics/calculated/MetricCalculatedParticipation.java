package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricValueUse;


public class MetricCalculatedParticipation extends MetricCalculated {


    private Dimension getUpperParticipationDimension(Dimension currentDimension, MetricCalculatedParticipationMetaData metaData) {
        return metaData.getParticipationAnalysisType().getUpperLevelDimension(currentDimension);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricCalculatedParticipationMetaData metaData = (MetricCalculatedParticipationMetaData) this.getMetaData();
        Calculation calculation = metaData.createCalculation();
        Double result = null;
        try {
            String titleColumnAV = calculation.getVariables().get(MetricCalculatedParticipationMetaData.COLUNA_AV_VARIABLE);

            Metric expression = metricLine.getMetrics().get(titleColumnAV);
            Double valor = expression.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
            if (valor != null) {
                calculation.setVariableValue(MetricCalculatedParticipationMetaData.COLUNA_AV_VARIABLE, valor);

                Dimension dimensionPai = this.getUpperParticipationDimension(metaData.DimensionReferenceAxis(metricLine), metaData);

                Double valueAbove = expression.getMetaData().calculaValorTotalParcial(dimensionPai, metaData.getDimensionOther(metricLine));
                if (valueAbove != null && valueAbove != 0) {

                    calculation.setVariableValue(MetricCalculatedParticipationMetaData.VALOR_NIVEL_ACIMA_VARIABLE, valueAbove);
                    result = calculation.calculateValue();
                } else {
                    result = (double) 0;
                }
            }
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
        }
        return result;
    }

    @Override
    public String searchMetricLineAlert(String function, Dimension dimensionLine, Dimension dimensionColumn) {
        if (dimensionLine.getParent() != null) {
            return super.searchMetricLineAlert(function, dimensionLine, dimensionColumn);
        }
        return null;
    }

    @Override
    public Double getValue(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricValueUse calculateLevel) {
        return calculate(metricsMap, metricLine, metricLineAnterior);
    }
}
