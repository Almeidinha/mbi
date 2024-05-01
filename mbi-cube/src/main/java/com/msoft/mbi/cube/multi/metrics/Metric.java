package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.aggregation.AggregatorMax;
import com.msoft.mbi.cube.multi.metrics.aggregation.AggregatorMedia;
import com.msoft.mbi.cube.multi.metrics.aggregation.AggregatorMinimum;
import com.msoft.mbi.cube.multi.metrics.aggregation.AggregatorSum;
import com.msoft.mbi.cube.multi.metrics.aggregation.Aggregator;
import com.msoft.mbi.cube.multi.metrics.aggregation.AggregatorEmpty;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import lombok.Getter;
import lombok.Setter;

public abstract class Metric {

    @Setter
    @Getter
    private MetricMetaData metaData = null;
    protected Aggregator aggregator;

    public String searchMetricLineAlert(String function, Dimension dimensionLine, Dimension dimensionColumn) {
        String styleName = null;
        List<ColorAlertConditionsMetrica> alerts = this.metaData.getColorsAlertLines(function);
        if (alerts != null) {
            for (ColorAlertConditionsMetrica colorsAlert : alerts) {
                if (colorsAlert.testaCondicao(dimensionLine, dimensionColumn, this.metaData.getCube())) {
                    styleName = CellProperty.CELL_PROPERTY_ALERTS_PREFIX + colorsAlert.getSequence();
                }
            }
        }
        return styleName;
    }

    public void somaValor(Double valor) {
        Double currentValue = this.aggregator.getAggregatorValue();
        if (currentValue == null) {
            currentValue = (double) 0;
        }
        currentValue += valor != null ? valor : 0;
        this.aggregator.setValue(currentValue);
    }

    public void setAggregator(String aggregationType) {
        if (MetricMetaData.SUM_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AggregatorSum();
        } else if (MetricMetaData.EMPTY.equalsIgnoreCase(aggregationType) || aggregationType == null || aggregationType.isEmpty()) {
            this.aggregator = new AggregatorEmpty();
        } else if (MetricMetaData.MEDIA_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AggregatorMedia();
        } else if (MetricMetaData.COUNT_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AggregatorSum();
        } else if (MetricMetaData.MINIMUM_AGGREGATION.equalsIgnoreCase(aggregationType)) {
            this.aggregator = new AggregatorMinimum();
        } else {
            this.aggregator = new AggregatorMax();
        }
    }

    public abstract Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior);

    public abstract Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior,
                                     MetricValueUse calculateLevel);

    public abstract Double getValue(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior);

}
