package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CuboMathParserException;
import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

import java.io.Serial;

public class MetricaCalculadaParticipacao extends MetricaCalculada {

    @Serial
    private static final long serialVersionUID = 7068703921985921810L;

    private Dimension getDimensaoAcimaParticipacao(Dimension dimensionAtual, MetricaCalculadaParticipacaoMetaData metaData) {
        return metaData.getAnaliseParticipacaoTipo().getDimensaoNivelAcima(dimensionAtual);
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {

        MetricaCalculadaParticipacaoMetaData metaData = (MetricaCalculadaParticipacaoMetaData) this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno = null;
        try {
            String tituloColunaAV = calculo.getVariables().get(MetricaCalculadaParticipacaoMetaData.COLUNA_AV_VARIABLE);

            Metrica expressao = linhaMetrica.getMetricas().get(tituloColunaAV);
            Double valor = expressao.getMetaData().calculaValorTotalParcial(linhaMetrica.getDimensionLinha(), linhaMetrica.getDimensionColuna());
            if (valor != null) {
                calculo.setValorVariable(MetricaCalculadaParticipacaoMetaData.COLUNA_AV_VARIABLE, (valor != null ? valor : 0));

                Dimension dimensionPai = this.getDimensaoAcimaParticipacao(metaData.getDimensaoEixoReferencia(linhaMetrica), metaData);

                Double valorAcima = expressao.getMetaData().calculaValorTotalParcial(dimensionPai, metaData.getDimensaoOutra(linhaMetrica));
                if (valorAcima != null && valorAcima != 0) {

                    calculo.setValorVariable(MetricaCalculadaParticipacaoMetaData.VALOR_NIVEL_ACIMA_VARIABLE, (valorAcima != null ? valorAcima : 0));
                    retorno = calculo.calculaValor();
                } else {
                    retorno = (double) 0;
                }
            }
        } catch (Exception ex) {
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
            throw parserException;
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
    public Double getValor(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
        return this.calcula(mapaMetricas, linhaMetrica, linhaMetricaAnterior);
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior, MetricaValorUtilizar nivelCalcular) {
        return calcula(mapaMetricas, linhaMetrica, linhaMetricaAnterior);
    }
}
