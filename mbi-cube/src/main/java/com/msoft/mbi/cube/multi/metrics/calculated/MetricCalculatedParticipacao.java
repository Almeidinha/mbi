package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;


public class MetricCalculatedParticipacao extends MetricCalculated {


    private Dimension getDimensaoAcimaParticipacao(Dimension dimensionAtual, MetricCalculatedParticipacaoMetaData metaData) {
        return metaData.getParticipationAnalysisType().getDimensaoNivelAcima(dimensionAtual);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricCalculatedParticipacaoMetaData metaData = (MetricCalculatedParticipacaoMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAV = calculo.getVariables().get(MetricCalculatedParticipacaoMetaData.COLUNA_AV_VARIABLE);

            Metric expressao = metricLine.getMetrics().get(tituloColunaAV);
            Double valor = expressao.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
            if (valor != null) {
                calculo.setValorVariable(MetricCalculatedParticipacaoMetaData.COLUNA_AV_VARIABLE, valor);

                Dimension dimensionPai = this.getDimensaoAcimaParticipacao(metaData.DimensionReferenceAxis(metricLine), metaData);

                Double valorAcima = expressao.getMetaData().calculaValorTotalParcial(dimensionPai, metaData.getDimensionOther(metricLine));
                if (valorAcima != null && valorAcima != 0) {

                    calculo.setValorVariable(MetricCalculatedParticipacaoMetaData.VALOR_NIVEL_ACIMA_VARIABLE, valorAcima);
                    retorno = calculo.calculaValor();
                } else {
                    retorno = (double) 0;
                }
            }
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
        }
        return retorno;
    }

    @Override
    public String searchMetricLineAlert(String function, Dimension dimensionLine, Dimension dimensionColumn) {
        if (dimensionLine.getParent() != null) {
            return super.searchMetricLineAlert(function, dimensionLine, dimensionColumn);
        }
        return null;
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar calculateLevel) {
        return calculate(metricsMap, metricLine, metricLineAnterior);
    }
}
