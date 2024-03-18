package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;


public class MetricCalculatedEvolucaoAH extends MetricCalculated {


    private Dimension getDimensaoAnterior(Dimension dimensionAtual, MetricCalculatedEvolucaoAHMetaData metaData) {
        return metaData.getAnaliseEvolucaoTipo().getDimensaoAnterior(dimensionAtual);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricCalculatedEvolucaoAHMetaData metaData = (MetricCalculatedEvolucaoAHMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAH = calculo.getVariables().get(MetricCalculatedEvolucaoAHMetaData.COLUNA_AH_VARIABLE);

            Metric metricReferencia = metricLine.getMetrics().get(tituloColunaAH);
            Double oValor = metricReferencia.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
            double valorAtual = oValor != null ? oValor : 0;

            double valorAnterior = 0;

            Dimension dimensionAnterior = this.getDimensaoAnterior(metricLine.getDimensionColumn(), metaData);

            Metric expressaoValor = metricLine.getMetrics().get(tituloColunaAH);
            oValor = expressaoValor.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), dimensionAnterior);
            valorAnterior = oValor != null ? oValor : 0;

            if (valorAtual != valorAnterior) {
                if (valorAnterior != 0) {

                    calculo.setValorVariable(MetricCalculatedEvolucaoAHMetaData.COLUNA_AH_VARIABLE, valorAtual);
                    calculo.setValorVariable(MetricCalculatedEvolucaoAHMetaData.COLUNA_VALOR_ANTERIOR_VARIABLE, valorAnterior);
                    double valorCalculado = calculo.calculaValor();
                    if (valorAnterior < 0) {
                        valorCalculado = valorCalculado * (-1);
                    }
                    retorno = valorCalculado;
                } else {
                    if (valorAtual > 0) {
                        retorno = 100.0;
                    } else if (valorAtual < 0) {
                        retorno = (double) -100;
                    }
                }
            } else {
                retorno = (double) 0;
            }
        } catch (Exception ex) {
            CubeMathParserException parserException = new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitle() + ".", ex);
            throw parserException;
        }
        return retorno;
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior);
    }

    @Override
    public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar calculateLevel) {
        return this.calculate(metricsMap, metricLine, metricLineAnterior);
    }

}
