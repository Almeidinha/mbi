package com.msoft.mbi.cube.multi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;
import com.msoft.mbi.cube.multi.dimension.DimensionLineNull;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAditiva;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedFunctionMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.multi.partialtotalization.PartialTotalizationApplyTypeSoma;

public class MetricsMap {

    private final Dimension dimensionNullLine;
    private final Dimension dimensionNullColumn;

    private final Map<String, Map<String, Metric>> metricStringHashMap = new HashMap<>();
    private final Cube cube;

    public MetricsMap(Cube cube) {
        this.cube = cube;
        dimensionNullLine = new DimensionLineNull(cube);
        dimensionNullColumn = new DimensionNullColumn(cube);
    }

    public MetricLine getMetricLine(Dimension dimensionLine, Dimension dimensionColumn) {
        Map<String, Metric> metrics = this.getMetricaMap(dimensionLine, dimensionColumn);
        return new MetricLine(dimensionLine, dimensionColumn, metrics);
    }

    public MetricLine getMetricLine(Dimension dimensionLine) {
        return this.getMetricLine(dimensionLine, dimensionNullColumn);
    }

    public void accumulateMetricLine(Dimension dimension, ResultSet set) throws SQLException {
        this.accumulateMetric(dimension, dimensionNullColumn, set);
    }

    public void accumulateMetricColumn(Dimension dimension, ResultSet set) throws SQLException {
        this.accumulateMetric(dimensionNullLine, dimension, set);
    }

    public void accumulateMetricLineOthers(Dimension dimensionLine, Dimension dimensionLineOthers) {
        this.accumulateMetricOthers(dimensionLine, dimensionLineOthers, dimensionNullColumn);
    }

    public void accumulateMetricColumnOthers(Dimension dimensionColumn) {
        this.accumulateMetricOthers(dimensionNullLine, dimensionNullLine, dimensionColumn);
    }

    public void accumulateMetric(Dimension dimensionLine, Dimension dimensionColumn, ResultSet set) throws SQLException {
        if (this.cube instanceof CubeDefaultFormat) {
            if (dimensionLine.getValue() != null && !dimensionLine.getValue().toString().equalsIgnoreCase("root")) {
                dimensionLine.setValue(dimensionLine.getValue() + "(*" + set.getRow() + "*)");
            }
        }

        String key = this.buildKey(dimensionLine, dimensionColumn);
        Map<String, Metric> mapActualMetrics = new HashMap<>();

        Map<String, Metric> mapMetrica = this.metricStringHashMap.computeIfAbsent(key, k -> new HashMap<>());

        MetricAditiva metrica;
        String title;
        String coluna;

        MetricLine currentMetricLine = new MetricLine(dimensionLine, dimensionColumn, mapActualMetrics);
        for (MetricAdditiveMetaData additiveMetaData : this.cube.getHierarchyMetricAdditive()) {
            title = additiveMetaData.getTitle();
            coluna = additiveMetaData.getColumn();

            metrica = (MetricAditiva) mapMetrica.get(title);

            if (metrica == null) {
                metrica = additiveMetaData.createMetrica();
                mapMetrica.put(title, metrica);
            }

            MetricAditiva currentMetricValue = additiveMetaData.createMetrica();
            mapActualMetrics.put(title, currentMetricValue);
            Double valor = additiveMetaData.getType().getValue(set, coluna);
            currentMetricValue.add(valor);

            metrica.add(valor);
        }

        MetricCalculated metricCalculated;

        for (MetricCalculatedMetaData calculatedMetaData: this.cube.getHierarchyMetricCalculated()) {
            title = calculatedMetaData.getTitle();

            metricCalculated = (MetricCalculated) mapMetrica.get(title);

            if (metricCalculated == null) {
                metricCalculated = calculatedMetaData.createMetrica();
                mapMetrica.put(title, metricCalculated);
            }
        }

        for (MetricCalculatedMetaData calculatedMetaData : this.cube.getHierarchyMetricCalculated()) {
            title = calculatedMetaData.getTitle();

            metricCalculated = (MetricCalculated) mapMetrica.get(title);

            if (!(calculatedMetaData instanceof MetricCalculatedFunctionMetaData)) {
                MetricCalculated currentDataMetric = calculatedMetaData.createMetrica();
                mapActualMetrics.put(title, currentDataMetric);

                MetricLine lineUse = calculatedMetaData.getAggregationApplyOrder().getMetricLineUse(currentMetricLine,
                        this);
                Double valor = metricCalculated.calculate(this, lineUse, null);
                metricCalculated.populateNewValue(valor);

                currentDataMetric.setValue(valor);
            }
        }
    }

    public void accumulateMetricOthers(Dimension dimensionLineToRemove, Dimension dimensionLinesOthers, Dimension dimensionColumn) {
        String otherKeys = this.buildKey(dimensionLinesOthers, dimensionColumn);
        Map<String, Metric> mapMetricOthers = this.metricStringHashMap.computeIfAbsent(otherKeys, k -> new HashMap<>());

        for (MetricAdditiveMetaData additiveMetaData: this.cube.getHierarchyMetricAdditive()) {
            String title = additiveMetaData.getTitle();
            MetricAditiva metric = (MetricAditiva) mapMetricOthers.get(title);
            if (metric == null) {
                metric = additiveMetaData.createMetrica();
                mapMetricOthers.put(title, metric);
            }
            PartialTotalizationApplyTypeSoma totalPartialLine = PartialTotalizationApplyTypeSoma.getInstance();
            Double valor = totalPartialLine.calculateValue(dimensionLineToRemove, dimensionColumn, additiveMetaData, this);

            metric.somaValor(valor);
        }


        for (MetricCalculatedMetaData metricaCalculadaMetaData: this.cube.getHierarchyMetricCalculated()) {
            String title = metricaCalculadaMetaData.getTitle();

            MetricCalculated metricCalculated = (MetricCalculated) mapMetricOthers.get(title);

            if (metricCalculated == null) {
                metricCalculated = metricaCalculadaMetaData.createMetrica();
                mapMetricOthers.put(title, metricCalculated);
            }

            if (!(metricaCalculadaMetaData instanceof MetricCalculatedFunctionMetaData)) {
                MetricLine line = new MetricLine(dimensionLinesOthers, this.dimensionNullColumn, mapMetricOthers);
                Double valor = metricCalculated.calculate(this, line, null);
                metricCalculated.setValue(valor);
            }
        }
    }

    public Map<String, Metric> getMetricaMap(Dimension dimensionLine, Dimension dimensionColumn) {
        String key = this.buildKey(dimensionLine, dimensionColumn);
        return this.metricStringHashMap.computeIfAbsent(key, k -> {
            Map<String, Metric> metricHashMap = new HashMap<>();
            for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
                Metric metric = metaData.createMetrica();
                metricHashMap.put(metaData.getTitle(), metric);
            }
            return metricHashMap;
        });
    }

    private String buildKey(Dimension dimensionLine, Dimension dimensionColumn) {
        return dimensionLine.getKeyValue() +
                "." +
                dimensionColumn.getKeyValue();
    }

    public void removeMetricLine(Dimension dimensionLine, Dimension dimensionColumn) {
        String key = this.buildKey(dimensionLine, dimensionColumn);
        this.metricStringHashMap.remove(key);
    }

    public void removeMetricLine(Dimension dimensionLine) {
        String key = this.buildKey(dimensionLine, dimensionNullColumn);
        this.metricStringHashMap.remove(key);
    }

}
