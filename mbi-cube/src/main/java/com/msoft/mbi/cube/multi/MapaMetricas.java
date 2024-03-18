package com.msoft.mbi.cube.multi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;
import com.msoft.mbi.cube.multi.dimension.DimensionLinhaNula;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.additive.MetricaAditiva;
import com.msoft.mbi.cube.multi.metrics.additive.MetricaAditivaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculada;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaFuncaoMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaMetaData;
import com.msoft.mbi.cube.multi.partialTotalization.TotalizacaoParcialAplicarTipoSoma;

public class MapaMetricas {

    private final Dimension dimensionNullLine;
    private final Dimension dimensionNullColumn;

    private final Map<String, Map<String, Metrica>> metricsMap = new HashMap<>();
    private final Cubo cube;

    public MapaMetricas(Cubo cube) {
        this.cube = cube;
        dimensionNullLine = new DimensionLinhaNula(cube);
        dimensionNullColumn = new DimensionColunaNula(cube);
    }

    public MetricLine getMetricLine(Dimension dimensionLine, Dimension dimensionColumn) {
        Map<String, Metrica> metrics = this.getMetricaMap(dimensionLine, dimensionColumn);
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
        Map<String, Metrica> mapActualMetrics = new HashMap<>();

        Map<String, Metrica> mapMetrica = this.metricsMap.computeIfAbsent(key, k -> new HashMap<>());

        Iterator<MetricaAditivaMetaData> isAdditive = this.cube.getHierarquiaMetricaAditiva().iterator();
        MetricaAditivaMetaData metricaMetaData;
        MetricaAditiva metrica;
        String titulo;
        String coluna;

        MetricLine metricLineAtual = new MetricLine(dimensionLine, dimensionColumn, mapActualMetrics);
        while (isAdditive.hasNext()) {
            metricaMetaData = isAdditive.next();
            titulo = metricaMetaData.getTitulo();
            coluna = metricaMetaData.getColuna();

            metrica = (MetricaAditiva) mapMetrica.get(titulo);

            if (metrica == null) {
                metrica = metricaMetaData.createMetrica();
                mapMetrica.put(titulo, metrica);
            }

            MetricaAditiva metricaValorAtual = metricaMetaData.createMetrica();
            mapActualMetrics.put(titulo, metricaValorAtual);
            Double valor = metricaMetaData.getTipo().getValor(set, coluna);
            metricaValorAtual.add(valor);

            metrica.add(valor);
        }

        Iterator<MetricaCalculadaMetaData> itCalculada = this.cube.getHierarquiaMetricaCalculada().iterator();
        MetricaCalculadaMetaData metricaCalculadaMetaData;
        MetricaCalculada metricaCalculada;

        while (itCalculada.hasNext()) {
            metricaCalculadaMetaData = itCalculada.next();
            titulo = metricaCalculadaMetaData.getTitulo();

            metricaCalculada = (MetricaCalculada) mapMetrica.get(titulo);

            if (metricaCalculada == null) {
                metricaCalculada = metricaCalculadaMetaData.createMetrica();
                mapMetrica.put(titulo, metricaCalculada);
            }
        }

        for (MetricaCalculadaMetaData calculadaMetaData : this.cube.getHierarquiaMetricaCalculada()) {
            metricaCalculadaMetaData = calculadaMetaData;
            titulo = metricaCalculadaMetaData.getTitulo();

            metricaCalculada = (MetricaCalculada) mapMetrica.get(titulo);

            if (!(metricaCalculadaMetaData instanceof MetricaCalculadaFuncaoMetaData)) {
                MetricaCalculada metricaCalculadaValorAtual = metricaCalculadaMetaData.createMetrica();
                mapActualMetrics.put(titulo, metricaCalculadaValorAtual);

                MetricLine metricLineUtilizar = metricaCalculadaMetaData.getAgregacaoAplicarOrdem().getLinhaMetricaUtilizar(metricLineAtual,
                        this);
                Double valor = metricaCalculada.calcula(this, metricLineUtilizar, (MetricLine) null);
                metricaCalculada.populaNovoValor(valor);

                metricaCalculadaValorAtual.setValor(valor);
            }
        }
    }

    public void accumulateMetricOthers(Dimension dimensionLinhaRemover, Dimension dimensionLinhaOutros, Dimension dimensionColumn) {
        String chaveOutros = this.buildKey(dimensionLinhaOutros, dimensionColumn);
        Map<String, Metrica> mapMetricaOutros = this.metricsMap.computeIfAbsent(chaveOutros, k -> new HashMap<>());

        Iterator<MetricaAditivaMetaData> itAditiva = this.cube.getHierarquiaMetricaAditiva().iterator();
        MetricaAditivaMetaData metricaMetaData;
        MetricaAditiva metrica;
        String titulo;

        while (itAditiva.hasNext()) {
            metricaMetaData = itAditiva.next();
            titulo = metricaMetaData.getTitulo();
            metrica = (MetricaAditiva) mapMetricaOutros.get(titulo);
            if (metrica == null) {
                metrica = metricaMetaData.createMetrica();
                mapMetricaOutros.put(titulo, metrica);
            }
            TotalizacaoParcialAplicarTipoSoma totalParcialLinha = TotalizacaoParcialAplicarTipoSoma.getInstance();
            Double valor = totalParcialLinha.calculaValor(dimensionLinhaRemover, dimensionColumn, metricaMetaData, this);

            metrica.somaValor(valor);
        }

        Iterator<MetricaCalculadaMetaData> itCalculada = this.cube.getHierarquiaMetricaCalculada().iterator();
        MetricaCalculadaMetaData metricaCalculadaMetaData;
        MetricaCalculada metricaCalculada;
        while (itCalculada.hasNext()) {
            metricaCalculadaMetaData = itCalculada.next();
            titulo = metricaCalculadaMetaData.getTitulo();

            metricaCalculada = (MetricaCalculada) mapMetricaOutros.get(titulo);

            if (metricaCalculada == null) {
                metricaCalculada = metricaCalculadaMetaData.createMetrica();
                mapMetricaOutros.put(titulo, metricaCalculada);
            }

            if (!(metricaCalculadaMetaData instanceof MetricaCalculadaFuncaoMetaData)) {
                MetricLine linha = new MetricLine(dimensionLinhaOutros, this.dimensionNullColumn, mapMetricaOutros);
                Double valor = metricaCalculada.calcula(this, linha, null);
                metricaCalculada.setValor(valor);
            }
        }
    }

    public Map<String, Metrica> getMetricaMap(Dimension dimensionLine, Dimension dimensionColumn) {
        String chave = this.buildKey(dimensionLine, dimensionColumn);
        Map<String, Metrica> mapMetrica = this.metricsMap.get(chave);
        if (mapMetrica == null) {
            mapMetrica = new HashMap<>();
            this.metricsMap.put(chave, mapMetrica);
            for (MetricaMetaData metaData : this.cube.getHierarquiaMetrica()) {
                Metrica metrica = metaData.createMetrica();
                mapMetrica.put(metaData.getTitulo(), metrica);
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
