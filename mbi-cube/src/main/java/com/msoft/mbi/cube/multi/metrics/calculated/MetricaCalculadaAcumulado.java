package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;


public class MetricaCalculadaAcumulado extends MetricaCalculada {


    @Override
    public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricaCalculadaAcumuladoMetaData metaData = (MetricaCalculadaAcumuladoMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAV = calculo.getVariables().get(MetricaCalculadaAcumuladoMetaData.COLUNA_AV_VARIABLE);
            Metrica metricaReferencia = metricLine.getMetrics().get(tituloColunaAV);
            Double valorAtual = metricaReferencia.getValor(mapaMetricas, metricLine, metricLineAnterior);
            calculo.setValorVariable(MetricaCalculadaAcumuladoMetaData.COLUNA_AV_VARIABLE, (valorAtual != null ? valorAtual : 0));

            Double valorAnterior = (double) 0;

            if (metaData instanceof MetricaCalculadaAcumuladoParticipacaoAVMetaData || metaData instanceof MetricaCalculadaAcumuladoValorAVMetaData) {
                if (metricLineAnterior != null) {

                    Metrica metricaValorAcumulado = metricLineAnterior.getMetrics().get(metaData.getTitulo());
                    valorAnterior = metricaValorAcumulado.getValor(mapaMetricas, metricLineAnterior, null);

                    if ((metaData instanceof MetricaCalculadaAcumuladoParticipacaoAVMetaData)) {

                        Dimension dimensionAtual = ((MetricaCalculadaAcumuladoParticipacaoAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(metricLine.getDimensionLine());
                        Dimension dimensionAnterior = ((MetricaCalculadaAcumuladoParticipacaoAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(metricLineAnterior.getDimensionLine());

                        if (dimensionAtual != dimensionAnterior && !dimensionAtual.getKeyValue().equals(dimensionAnterior.getKeyValue())) {
                            valorAnterior = (double) 0;
                        }
                    }

                    if ((metaData instanceof MetricaCalculadaAcumuladoValorAVMetaData)) {

                        Dimension dimensionAtual = ((MetricaCalculadaAcumuladoValorAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(metricLine.getDimensionLine());
                        Dimension dimensionAnterior = ((MetricaCalculadaAcumuladoValorAVMetaData) metaData).getAnaliseParticipacaoTipo().getDimensaoNivelAcima(metricLineAnterior.getDimensionLine());

                        if (dimensionAtual != dimensionAnterior && !dimensionAtual.getKeyValue().equals(dimensionAnterior.getKeyValue())) {
                            valorAnterior = (double) 0;
                        }
                    }
                }
            }

            if (metaData instanceof MetricaCalculadaAcumuladoParticipacaoAHMetaData) {
                Dimension dimensionAnterior = this.getDimensaoAnterior(metricLine.getDimensionColumn(), metaData);

                if (dimensionAnterior != null) {
                    Metrica expressaoValor = metricLine.getMetrics().get(metaData.getTitulo());
                    valorAnterior = expressaoValor.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), dimensionAnterior);
                }
            }

            calculo.setValorVariable(MetricaCalculadaAcumuladoMetaData.COLUNA_VALOR_ANTERIOR_VARIABLE, (valorAnterior != null ? valorAnterior : 0));
            retorno = calculo.calculaValor();
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível relizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
        }
        return retorno;
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar nivelCalcular) {
        return calcula(mapaMetricas, metricLine, metricLineAnterior);
    }

    private Dimension getDimensaoAnterior(Dimension dimensionAtual, MetricaCalculadaAcumuladoMetaData metaData) {
        return dimensionAtual.getPreviousDimension(metaData.getAnaliseParticipacaoTipo().getDimensaoNivelAcima(dimensionAtual));
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {
        if (this.agregador.getValorAgregado() == null) {
            this.agregador.setValor(this.calcula(mapaMetricas, metricLine, metricLineAnterior));
        }
        return this.agregador.getValorAgregado();
    }

}
