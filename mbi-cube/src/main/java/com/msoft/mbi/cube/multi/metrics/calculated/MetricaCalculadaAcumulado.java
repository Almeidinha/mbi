package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CuboMathParserException;
import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

import java.io.Serial;

public class MetricaCalculadaAcumulado extends MetricaCalculada {

    @Serial
    private static final long serialVersionUID = 7068703921985921810L;

    @Override
    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
        //o metadado da metrica calculada acumulada
        MetricaCalculadaAcumuladoMetaData metaData = (MetricaCalculadaAcumuladoMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAV = calculo.getVariables().get(MetricaCalculadaAcumuladoMetaData.COLUNA_AV_VARIABLE);
            Metrica metricaReferencia = linhaMetrica.getMetricas().get(tituloColunaAV);
            Double valorAtual = metricaReferencia.getValor(mapaMetricas, linhaMetrica, linhaMetricaAnterior);
            calculo.setValorVariable(MetricaCalculadaAcumuladoMetaData.COLUNA_AV_VARIABLE, (valorAtual != null ? valorAtual : 0));

            Double valorAnterior = (double) 0;

            if (metaData instanceof MetricaCalculadaAcumuladoParticipacaoAVMetaData || metaData instanceof MetricaCalculadaAcumuladoValorAVMetaData) {
                if (linhaMetricaAnterior != null) {

                    Metrica metricaValorAcumulado = linhaMetricaAnterior.getMetricas().get(metaData.getTitulo());
                    valorAnterior = metricaValorAcumulado.getValor(mapaMetricas, linhaMetricaAnterior, null);

                    if ((metaData instanceof MetricaCalculadaAcumuladoParticipacaoAVMetaData)) {

                        Dimension dimensionAtual = ((MetricaCalculadaAcumuladoParticipacaoAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(linhaMetrica.getDimensionLinha());
                        Dimension dimensionAnterior = ((MetricaCalculadaAcumuladoParticipacaoAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(linhaMetricaAnterior.getDimensionLinha());

                        if (dimensionAtual != dimensionAnterior && !dimensionAtual.getKeyValue().equals(dimensionAnterior.getKeyValue())) {
                            valorAnterior = (double) 0;
                        }
                    }

                    if ((metaData instanceof MetricaCalculadaAcumuladoValorAVMetaData)) {

                        Dimension dimensionAtual = ((MetricaCalculadaAcumuladoValorAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(linhaMetrica.getDimensionLinha());
                        Dimension dimensionAnterior = ((MetricaCalculadaAcumuladoValorAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(linhaMetricaAnterior.getDimensionLinha());

                        if (dimensionAtual != dimensionAnterior && !dimensionAtual.getKeyValue().equals(dimensionAnterior.getKeyValue())) {
                            valorAnterior = (double) 0;
                        }
                    }
                }
            }

            if (metaData instanceof MetricaCalculadaAcumuladoParticipacaoAHMetaData) {
                Dimension dimensionAnterior = this.getDimensaoAnterior(linhaMetrica.getDimensionColuna(), metaData);

                if (dimensionAnterior != null) {
                    Metrica expressaoValor = linhaMetrica.getMetricas().get(metaData.getTitulo());
                    valorAnterior = expressaoValor.getMetaData().calculaValorTotalParcial(linhaMetrica.getDimensionLinha(), dimensionAnterior);
                }
            }

            calculo.setValorVariable(MetricaCalculadaAcumuladoMetaData.COLUNA_VALOR_ANTERIOR_VARIABLE, (valorAnterior != null ? valorAnterior : 0));
            retorno = calculo.calculaValor();
        } catch (Exception ex) {
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível relizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
            throw parserException;
        }
        return retorno;
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior, MetricaValorUtilizar nivelCalcular) {
        return calcula(mapaMetricas, linhaMetrica, linhaMetricaAnterior);
    }

    private Dimension getDimensaoAnterior(Dimension dimensionAtual, MetricaCalculadaAcumuladoMetaData metaData) {
        return dimensionAtual.getPreviousDimension(metaData.getAnaliseParticipacaoTipo().getDimensaoNivelAcima(dimensionAtual));
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
        if (this.agregador.getValorAgregado() == null) {
            this.agregador.setValor(this.calcula(mapaMetricas, linhaMetrica, linhaMetricaAnterior));
        }
        return this.agregador.getValorAgregado();
    }

}
