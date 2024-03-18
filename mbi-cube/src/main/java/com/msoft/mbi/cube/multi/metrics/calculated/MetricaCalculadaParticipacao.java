package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

import java.io.Serial;

public class MetricaCalculadaParticipacao extends MetricaCalculada {


    private Dimension getDimensaoAcimaParticipacao(Dimension dimensionAtual, MetricaCalculadaParticipacaoMetaData metaData) {
        return metaData.getAnaliseParticipacaoTipo().getDimensaoNivelAcima(dimensionAtual);
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {

        MetricaCalculadaParticipacaoMetaData metaData = (MetricaCalculadaParticipacaoMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAV = calculo.getVariables().get(MetricaCalculadaParticipacaoMetaData.COLUNA_AV_VARIABLE);

            Metrica expressao = metricLine.getMetrics().get(tituloColunaAV);
            Double valor = expressao.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
            if (valor != null) {
                calculo.setValorVariable(MetricaCalculadaParticipacaoMetaData.COLUNA_AV_VARIABLE, valor);

                Dimension dimensionPai = this.getDimensaoAcimaParticipacao(metaData.getDimensaoEixoReferencia(metricLine), metaData);

                Double valorAcima = expressao.getMetaData().calculaValorTotalParcial(dimensionPai, metaData.getDimensaoOutra(metricLine));
                if (valorAcima != null && valorAcima != 0) {

                    calculo.setValorVariable(MetricaCalculadaParticipacaoMetaData.VALOR_NIVEL_ACIMA_VARIABLE, valorAcima);
                    retorno = calculo.calculaValor();
                } else {
                    retorno = (double) 0;
                }
            }
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
        }
        return retorno;
    }

    @Override
    public String buscaAlertaMetricaLinha(String funcao, Dimension dimensionLinha, Dimension dimensionColuna) {
        if (dimensionLinha.getParent() != null) {
            return super.buscaAlertaMetricaLinha(funcao, dimensionLinha, dimensionColuna);
        }
        return null;
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
