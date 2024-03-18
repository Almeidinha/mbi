package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorMaximo;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorMedia;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorMinimo;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorSoma;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorTipo;
import com.msoft.mbi.cube.multi.metrics.aggregation.AgregadorVazio;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import lombok.Getter;
import lombok.Setter;

public abstract class Metric {

    @Setter
    @Getter
    private MetricMetaData metaData = null;
    protected AgregadorTipo aggregator;

    public String searchMetricLineAlert(String function, Dimension dimensionLine, Dimension dimensionColumn) {
        String nomeEstilo = null;
        List<ColorAlertConditionsMetrica> alerts = this.metaData.getColorsAlertLines(function);
        if (alerts != null) {
            for (ColorAlertConditionsMetrica colorsAlert : alerts) {
                if (colorsAlert.testaCondicao(dimensionLine, dimensionColumn, this.metaData.getCube())) {
                    nomeEstilo = CellProperty.PROPRIEDADE_CELULA_ALERTAS_PREFIXO + colorsAlert.getSequence();
                }
            }
        }
        return nomeEstilo;
    }

    public void somaValor(Double valor) {
        Double valorAtual = this.aggregator.getValorAgregado();
        if (valorAtual == null) {
            valorAtual = (double) 0;
        }
        valorAtual += valor != null ? valor : 0;
        this.aggregator.setValor(valorAtual);
    }

    public void setAggregator(String aggregationType) {
        if (MetricMetaData.SUM_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AgregadorSoma();
        } else if (MetricMetaData.EMPTY_AGGREGATION.equalsIgnoreCase(aggregationType) || aggregationType == null || "".equals(aggregationType)) {
            this.aggregator = new AgregadorVazio();
        } else if (MetricMetaData.MEDIA_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AgregadorMedia();
        } else if (MetricMetaData.COUNT_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AgregadorSoma();
        } else if (MetricMetaData.MINIMUM_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AgregadorMinimo();
        } else {
            this.aggregator = new AgregadorMaximo();
        }
    }

    public abstract Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior);

    public abstract Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior,
                                     MetricaValorUtilizar calculateLevel);

    public abstract Double getValor(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior);

}
