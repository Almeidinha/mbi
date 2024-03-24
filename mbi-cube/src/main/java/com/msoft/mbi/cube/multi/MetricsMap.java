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
import com.msoft.mbi.cube.multi.partialTotalization.PartialTotalizationApplyTypeSoma;

public class MetricsMap {

    private final Dimension dimensionNullLine;
    private final Dimension dimensionNullColumn;

    private final Map<String, Map<String, Metric>> metricsMap = new HashMap<>();
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

        Map<String, Metric> mapMetrica = this.metricsMap.computeIfAbsent(key, k -> new HashMap<>());

        Iterator<MetricAdditiveMetaData> isAdditive = this.cube.getHierarchyMetricAdditive().iterator();
        MetricAdditiveMetaData metricaMetaData;
        MetricAditiva metrica;
        String titulo;
        String coluna;

        MetricLine currentMetricLine = new MetricLine(dimensionLine, dimensionColumn, mapActualMetrics);
        while (isAdditive.hasNext()) {
            metricaMetaData = isAdditive.next();
            titulo = metricaMetaData.getTitle();
            coluna = metricaMetaData.getColumn();

            metrica = (MetricAditiva) mapMetrica.get(titulo);

            if (metrica == null) {
                metrica = metricaMetaData.createMetrica();
                mapMetrica.put(titulo, metrica);
            }

            MetricAditiva currentMetricValue = metricaMetaData.createMetrica();
            mapActualMetrics.put(titulo, currentMetricValue);
            Double valor = metricaMetaData.getType().getValue(set, coluna);
            currentMetricValue.add(valor);

            metrica.add(valor);
        }

        Iterator<MetricCalculatedMetaData> itCalculada = this.cube.getHierarchyMetricCalculated().iterator();
        MetricCalculatedMetaData metricaCalculadaMetaData;
        MetricCalculated metricCalculated;

        while (itCalculada.hasNext()) {
            metricaCalculadaMetaData = itCalculada.next();
            titulo = metricaCalculadaMetaData.getTitle();

            metricCalculated = (MetricCalculated) mapMetrica.get(titulo);

            if (metricCalculated == null) {
                metricCalculated = metricaCalculadaMetaData.createMetrica();
                mapMetrica.put(titulo, metricCalculated);
            }
        }

        for (MetricCalculatedMetaData calculadaMetaData : this.cube.getHierarchyMetricCalculated()) {
            metricaCalculadaMetaData = calculadaMetaData;
            titulo = metricaCalculadaMetaData.getTitle();

            metricCalculated = (MetricCalculated) mapMetrica.get(titulo);

            if (!(metricaCalculadaMetaData instanceof MetricCalculatedFunctionMetaData)) {
                MetricCalculated metricCalculatedValorAtual = metricaCalculadaMetaData.createMetrica();
                mapActualMetrics.put(titulo, metricCalculatedValorAtual);

                MetricLine metricLineUtilizar = metricaCalculadaMetaData.getAggregationApplyOrder().getMetricLineUse(currentMetricLine,
                        this);
                Double valor = metricCalculated.calculate(this, metricLineUtilizar, (MetricLine) null);
                metricCalculated.populateNewValue(valor);

                metricCalculatedValorAtual.setValue(valor);
            }
        }
    }

    public void accumulateMetricOthers(Dimension dimensionLineToRemove, Dimension dimensionLinesOthers, Dimension dimensionColumn) {
        String otherKeys = this.buildKey(dimensionLinesOthers, dimensionColumn);
        Map<String, Metric> mapMetricaOutros = this.metricsMap.computeIfAbsent(otherKeys, k -> new HashMap<>());

        Iterator<MetricAdditiveMetaData> itAditiva = this.cube.getHierarchyMetricAdditive().iterator();
        MetricAdditiveMetaData metricaMetaData;
        MetricAditiva metrica;
        String titulo;

        while (itAditiva.hasNext()) {
            metricaMetaData = itAditiva.next();
            titulo = metricaMetaData.getTitle();
            metrica = (MetricAditiva) mapMetricaOutros.get(titulo);
            if (metrica == null) {
                metrica = metricaMetaData.createMetrica();
                mapMetricaOutros.put(titulo, metrica);
            }
            PartialTotalizationApplyTypeSoma totalParcialLinha = PartialTotalizationApplyTypeSoma.getInstance();
            Double valor = totalParcialLinha.calculateValue(dimensionLineToRemove, dimensionColumn, metricaMetaData, this);

            metrica.somaValor(valor);
        }

        Iterator<MetricCalculatedMetaData> itCalculada = this.cube.getHierarchyMetricCalculated().iterator();
        MetricCalculatedMetaData metricaCalculadaMetaData;
        MetricCalculated metricCalculated;
        while (itCalculada.hasNext()) {
            metricaCalculadaMetaData = itCalculada.next();
            titulo = metricaCalculadaMetaData.getTitle();

            metricCalculated = (MetricCalculated) mapMetricaOutros.get(titulo);

            if (metricCalculated == null) {
                metricCalculated = metricaCalculadaMetaData.createMetrica();
                mapMetricaOutros.put(titulo, metricCalculated);
            }

            if (!(metricaCalculadaMetaData instanceof MetricCalculatedFunctionMetaData)) {
                MetricLine linha = new MetricLine(dimensionLinesOthers, this.dimensionNullColumn, mapMetricaOutros);
                Double valor = metricCalculated.calculate(this, linha, null);
                metricCalculated.setValue(valor);
            }
        }
    }

    public Map<String, Metric> getMetricaMap(Dimension dimensionLine, Dimension dimensionColumn) {
        String key = this.buildKey(dimensionLine, dimensionColumn);
        Map<String, Metric> mapMetrica = this.metricsMap.get(key);
        if (mapMetrica == null) {
            mapMetrica = new HashMap<>();
            this.metricsMap.put(key, mapMetrica);
            for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
                Metric metric = metaData.createMetrica();
                mapMetrica.put(metaData.getTitle(), metric);
            }
        }
        return mapMetrica;
    }

    private String buildKey(Dimension dimensionLine, Dimension dimensionColumn) {
        return dimensionLine.getKeyValue() +
                "." +
                dimensionColumn.getKeyValue();
    }

    public void removeMetricLine(Dimension dimensionLine, Dimension dimensionColumn) {
        String key = this.buildKey(dimensionLine, dimensionColumn);
        this.metricsMap.remove(key);
    }

    public void removeMetricLine(Dimension dimensionLine) {
        String key = this.buildKey(dimensionLine, dimensionNullColumn);
        this.metricsMap.remove(key);
    }

}
