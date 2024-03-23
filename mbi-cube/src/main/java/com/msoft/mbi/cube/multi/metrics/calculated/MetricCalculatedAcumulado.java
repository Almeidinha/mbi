package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;


public class MetricCalculatedAcumulado extends MetricCalculated {


    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricCalculatedAcumuladoMetaData metaData = (MetricCalculatedAcumuladoMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAV = calculo.getVariables().get(MetricCalculatedAcumuladoMetaData.COLUNA_AV_VARIABLE);
            Metric metricReferencia = metricLine.getMetrics().get(tituloColunaAV);
            Double valorAtual = metricReferencia.getValor(metricsMap, metricLine, metricLineAnterior);
            calculo.setValorVariable(MetricCalculatedAcumuladoMetaData.COLUNA_AV_VARIABLE, (valorAtual != null ? valorAtual : 0));

            Double valorAnterior = (double) 0;

            if (metaData instanceof MetricCalculatedAcumuladoParticipacaoAVMetaData || metaData instanceof MetricCalculatedAcumuladoValorAVMetaData) {
                if (metricLineAnterior != null) {

                    Metric metricValorAcumulado = metricLineAnterior.getMetrics().get(metaData.getTitle());
                    valorAnterior = metricValorAcumulado.getValor(metricsMap, metricLineAnterior, null);

                    if ((metaData instanceof MetricCalculatedAcumuladoParticipacaoAVMetaData)) {

                        Dimension dimensionAtual = ((MetricCalculatedAcumuladoParticipacaoAVMetaData) metaData).getParticipationAnalysisType().getDimensaoNivelAcima(metricLine.getDimensionLine());
                        Dimension dimensionAnterior = ((MetricCalculatedAcumuladoParticipacaoAVMetaData) metaData).getParticipationAnalysisType().getDimensaoNivelAcima(metricLineAnterior.getDimensionLine());

                        if (dimensionAtual != dimensionAnterior && !dimensionAtual.getKeyValue().equals(dimensionAnterior.getKeyValue())) {
                            valorAnterior = (double) 0;
                        }
                    }

                    if ((metaData instanceof MetricCalculatedAcumuladoValorAVMetaData)) {

                        Dimension dimensionAtual = ((MetricCalculatedAcumuladoValorAVMetaData) metaData).getParticipationAnalysisType().getDimensaoNivelAcima(metricLine.getDimensionLine());
                        Dimension dimensionAnterior = ((MetricCalculatedAcumuladoValorAVMetaData) metaData).getParticipationAnalysisType().getDimensaoNivelAcima(metricLineAnterior.getDimensionLine());

                        if (dimensionAtual != dimensionAnterior && !dimensionAtual.getKeyValue().equals(dimensionAnterior.getKeyValue())) {
                            valorAnterior = (double) 0;
                        }
                    }
                }
            }

            if (metaData instanceof MetricCalculatedAcumuladoParticipacaoAHMetaData) {
                Dimension dimensionAnterior = this.getDimensaoAnterior(metricLine.getDimensionColumn(), metaData);

                if (dimensionAnterior != null) {
                    Metric expressaoValor = metricLine.getMetrics().get(metaData.getTitle());
                    valorAnterior = expressaoValor.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), dimensionAnterior);
                }
            }

            calculo.setValorVariable(MetricCalculatedAcumuladoMetaData.COLUNA_VALOR_ANTERIOR_VARIABLE, (valorAnterior != null ? valorAnterior : 0));
            retorno = calculo.calculaValor();
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível relizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
        }
        return retorno;
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar calculateLevel) {
        return calculate(metricsMap, metricLine, metricLineAnterior);
    }

    private Dimension getDimensaoAnterior(Dimension dimensionAtual, MetricCalculatedAcumuladoMetaData metaData) {
        return dimensionAtual.getPreviousDimension(metaData.getParticipationAnalysisType().getDimensaoNivelAcima(dimensionAtual));
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        if (this.aggregator.getAggregatorValue() == null) {
            this.aggregator.setValue(this.calculate(metricsMap, metricLine, metricLineAnterior));
        }
        return this.aggregator.getAggregatorValue();
    }

}
