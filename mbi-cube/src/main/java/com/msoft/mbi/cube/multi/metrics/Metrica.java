package com.msoft.mbi.cube.multi.metrics;

import java.io.Serializable;
import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorMaximo;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorMedia;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorMinimo;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorSoma;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorTipo;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorVazio;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public abstract class Metrica implements Serializable {

    private MetricaMetaData metaData = null;
    protected AgregadorTipo agregador;

    public MetricaMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetricaMetaData metaData) {
        this.metaData = metaData;
    }

    public String buscaAlertaMetricaLinha(String funcao, Dimension dimensionLinha, Dimension dimensionColuna) {
        String nomeEstilo = null;
        List<ColorAlertConditionsMetrica> alertas = this.metaData.getAlertasCoresLinha(funcao);
        if (alertas != null) {
            for (ColorAlertConditionsMetrica alertaCores : alertas) {
                if (alertaCores.testaCondicao(dimensionLinha, dimensionColuna, this.metaData.getCubo())) {
                    nomeEstilo = CellProperty.PROPRIEDADE_CELULA_ALERTAS_PREFIXO + alertaCores.getSequence();
                }
            }
        }
        return nomeEstilo;
    }

    public void somaValor(Double valor) {
        Double valorAtual = this.agregador.getValorAgregado();
        if (valorAtual == null) {
            valorAtual = (double) 0;
        }
        valorAtual += valor != null ? valor : 0;
        this.agregador.setValor(valorAtual);
    }

    public void setAgregador(String agregacaoTipo) {
        if (MetricaMetaData.AGREGACAO_SOMATORIO.equalsIgnoreCase(agregacaoTipo)) {
            this.agregador = new AgregadorSoma();
        } else if (MetricaMetaData.AGREGACAO_VAZIO.equalsIgnoreCase(agregacaoTipo) || agregacaoTipo == null || "".equals(agregacaoTipo)) {
            this.agregador = new AgregadorVazio();
        } else if (MetricaMetaData.AGREGACAO_MEDIA.equalsIgnoreCase(agregacaoTipo)) {
            this.agregador = new AgregadorMedia();
        } else if (MetricaMetaData.AGREGACAO_CONTAGEM.equalsIgnoreCase(agregacaoTipo)) {
            this.agregador = new AgregadorSoma();
        } else if (MetricaMetaData.AGREGACAO_MINIMO.equalsIgnoreCase(agregacaoTipo)) {
            this.agregador = new AgregadorMinimo();
        } else {
            this.agregador = new AgregadorMaximo();
        }
    }

    public abstract Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior);

    public abstract Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior,
                                   MetricaValorUtilizar nivelCalcular);

    public abstract Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior);

}
