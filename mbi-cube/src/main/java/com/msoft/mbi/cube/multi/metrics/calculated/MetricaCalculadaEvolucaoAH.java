package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;


public class MetricaCalculadaEvolucaoAH extends MetricaCalculada {


    private Dimension getDimensaoAnterior(Dimension dimensionAtual, MetricaCalculadaEvolucaoAHMetaData metaData) {
        return metaData.getAnaliseEvolucaoTipo().getDimensaoAnterior(dimensionAtual);
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricaCalculadaEvolucaoAHMetaData metaData = (MetricaCalculadaEvolucaoAHMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAH = calculo.getVariables().get(MetricaCalculadaEvolucaoAHMetaData.COLUNA_AH_VARIABLE);

            Metrica metricaReferencia = metricLine.getMetrics().get(tituloColunaAH);
            Double oValor = metricaReferencia.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
            double valorAtual = oValor != null ? oValor : 0;

            double valorAnterior = 0;

            Dimension dimensionAnterior = this.getDimensaoAnterior(metricLine.getDimensionColumn(), metaData);

            Metrica expressaoValor = metricLine.getMetrics().get(tituloColunaAH);
            oValor = expressaoValor.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), dimensionAnterior);
            valorAnterior = oValor != null ? oValor : 0;

            if (valorAtual != valorAnterior) {
                if (valorAnterior != 0) {

                    calculo.setValorVariable(MetricaCalculadaEvolucaoAHMetaData.COLUNA_AH_VARIABLE, valorAtual);
                    calculo.setValorVariable(MetricaCalculadaEvolucaoAHMetaData.COLUNA_VALOR_ANTERIOR_VARIABLE, valorAnterior);
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
            CubeMathParserException parserException = new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
            throw parserException;
        }
        return retorno;
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calcula(mapaMetricas, metricLine, metricLineAnterior);
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar nivelCalcular) {
        return calcula(mapaMetricas, metricLine, metricLineAnterior);
    }

}
