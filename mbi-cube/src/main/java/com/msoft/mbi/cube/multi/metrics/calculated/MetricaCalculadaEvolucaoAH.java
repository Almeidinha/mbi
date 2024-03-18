package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CuboMathParserException;
import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

import java.io.Serial;

public class MetricaCalculadaEvolucaoAH extends MetricaCalculada {

    @Serial
    private static final long serialVersionUID = 7068703921985921810L;

    private Dimension getDimensaoAnterior(Dimension dimensionAtual, MetricaCalculadaEvolucaoAHMetaData metaData) {
        return metaData.getAnaliseEvolucaoTipo().getDimensaoAnterior(dimensionAtual);
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {

        MetricaCalculadaEvolucaoAHMetaData metaData = (MetricaCalculadaEvolucaoAHMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAH = calculo.getVariables().get(MetricaCalculadaEvolucaoAHMetaData.COLUNA_AH_VARIABLE);

            Metrica metricaReferencia = linhaMetrica.getMetricas().get(tituloColunaAH);
            Double oValor = metricaReferencia.getMetaData().calculaValorTotalParcial(linhaMetrica.getDimensionLinha(), linhaMetrica.getDimensionColuna());
            double valorAtual = oValor != null ? oValor : 0;

            double valorAnterior = 0;

            Dimension dimensionAnterior = this.getDimensaoAnterior(linhaMetrica.getDimensionColuna(), metaData);

            Metrica expressaoValor = linhaMetrica.getMetricas().get(tituloColunaAH);
            oValor = expressaoValor.getMetaData().calculaValorTotalParcial(linhaMetrica.getDimensionLinha(), dimensionAnterior);
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
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
            throw parserException;
        }
        return retorno;
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
        return this.calcula(mapaMetricas, linhaMetrica, linhaMetricaAnterior);
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior, MetricaValorUtilizar nivelCalcular) {
        return calcula(mapaMetricas, linhaMetrica, linhaMetricaAnterior);
    }

}
