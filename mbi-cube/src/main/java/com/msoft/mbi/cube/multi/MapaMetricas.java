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

    private final Dimension dimensionLinhaNula;
    private final Dimension dimensionColunaNula;

    private final Map<String, Map<String, Metrica>> mapMetricas = new HashMap<>();
    private final Cubo cubo;

    public MapaMetricas(Cubo cubo) {
        this.cubo = cubo;
        dimensionLinhaNula = new DimensionLinhaNula(cubo);
        dimensionColunaNula = new DimensionColunaNula(cubo);
    }

    public LinhaMetrica getLinhaMetrica(Dimension dimensionLinha, Dimension dimensionColuna) {
        Map<String, Metrica> metricas = this.getMapMetricas(dimensionLinha, dimensionColuna);
        return new LinhaMetrica(dimensionLinha, dimensionColuna, metricas);
    }

    public LinhaMetrica getLinhaMetrica(Dimension dimensionLinha) {
        return this.getLinhaMetrica(dimensionLinha, dimensionColunaNula);
    }

    public void acumulaMetricaLinha(Dimension dimension, ResultSet set) throws SQLException {
        this.acumulaMetrica(dimension, dimensionColunaNula, set);
    }

    public void acumulaMetricaColuna(Dimension dimension, ResultSet set) throws SQLException {
        this.acumulaMetrica(dimensionLinhaNula, dimension, set);
    }

    public void acumulaMetricaLinhaOutros(Dimension dimensionLinha, Dimension dimensaLinhaOutros) {
        this.acumulaMetricaOutros(dimensionLinha, dimensaLinhaOutros, dimensionColunaNula);
    }

    public void acumulaMetricaColunaOutros(Dimension dimensionColuna) {
        this.acumulaMetricaOutros(dimensionLinhaNula, dimensionLinhaNula, dimensionColuna);
    }

    public void acumulaMetrica(Dimension dimensionLinha, Dimension dimensionColuna, ResultSet set) throws SQLException {
        if (this.cubo instanceof CuboFormatoPadrao) {
            if (dimensionLinha.getValue() != null && !dimensionLinha.getValue().toString().equalsIgnoreCase("root")) {
                dimensionLinha.setValue(dimensionLinha.getValue() + "(*" + set.getRow() + "*)");
            }
        }

        String chave = this.formaChave(dimensionLinha, dimensionColuna);
        Map<String, Metrica> mapMetricaAtual = new HashMap<>();

        Map<String, Metrica> mapMetrica = this.mapMetricas.computeIfAbsent(chave, k -> new HashMap<>());

        Iterator<MetricaAditivaMetaData> itAditiva = this.cubo.getHierarquiaMetricaAditiva().iterator();
        MetricaAditivaMetaData metricaMetaData;
        MetricaAditiva metrica;
        String titulo;
        String coluna;

        LinhaMetrica linhaMetricaAtual = new LinhaMetrica(dimensionLinha, dimensionColuna, mapMetricaAtual);
        while (itAditiva.hasNext()) {
            metricaMetaData = itAditiva.next();
            titulo = metricaMetaData.getTitulo();
            coluna = metricaMetaData.getColuna();

            metrica = (MetricaAditiva) mapMetrica.get(titulo);

            if (metrica == null) {
                metrica = metricaMetaData.createMetrica();
                mapMetrica.put(titulo, metrica);
            }

            MetricaAditiva metricaValorAtual = metricaMetaData.createMetrica();
            mapMetricaAtual.put(titulo, metricaValorAtual);
            Double valor = (Double) metricaMetaData.getTipo().getValor(set, coluna);
            metricaValorAtual.add(valor);

            metrica.add(valor);
        }

        Iterator<MetricaCalculadaMetaData> itCalculada = this.cubo.getHierarquiaMetricaCalculada().iterator();
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

        for (MetricaCalculadaMetaData calculadaMetaData : this.cubo.getHierarquiaMetricaCalculada()) {
            metricaCalculadaMetaData = calculadaMetaData;
            titulo = metricaCalculadaMetaData.getTitulo();

            metricaCalculada = (MetricaCalculada) mapMetrica.get(titulo);

            if (!(metricaCalculadaMetaData instanceof MetricaCalculadaFuncaoMetaData)) {
                MetricaCalculada metricaCalculadaValorAtual = metricaCalculadaMetaData.createMetrica();
                mapMetricaAtual.put(titulo, metricaCalculadaValorAtual);

                LinhaMetrica linhaMetricaUtilizar = metricaCalculadaMetaData.getAgregacaoAplicarOrdem().getLinhaMetricaUtilizar(linhaMetricaAtual,
                        this);
                Double valor = metricaCalculada.calcula(this, linhaMetricaUtilizar, (LinhaMetrica) null);
                metricaCalculada.populaNovoValor(valor);

                metricaCalculadaValorAtual.setValor(valor);
            }
        }
    }

    public void acumulaMetricaOutros(Dimension dimensionLinhaRemover, Dimension dimensionLinhaOutros, Dimension dimensionColuna) {
        String chaveOutros = this.formaChave(dimensionLinhaOutros, dimensionColuna);
        Map<String, Metrica> mapMetricaOutros = this.mapMetricas.computeIfAbsent(chaveOutros, k -> new HashMap<>());

        Iterator<MetricaAditivaMetaData> itAditiva = this.cubo.getHierarquiaMetricaAditiva().iterator();
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
            Double valor = totalParcialLinha.calculaValor(dimensionLinhaRemover, dimensionColuna, metricaMetaData, this);

            metrica.somaValor(valor);
        }

        Iterator<MetricaCalculadaMetaData> itCalculada = this.cubo.getHierarquiaMetricaCalculada().iterator();
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
                LinhaMetrica linha = new LinhaMetrica(dimensionLinhaOutros, this.dimensionColunaNula, mapMetricaOutros);
                Double valor = metricaCalculada.calcula(this, linha, null);
                metricaCalculada.setValor(valor);
            }
        }
    }

    public Map<String, Metrica> getMapMetricas(Dimension dimensionLinha, Dimension dimensionColuna) {
        String chave = this.formaChave(dimensionLinha, dimensionColuna);
        Map<String, Metrica> mapMetrica = this.mapMetricas.get(chave);
        if (mapMetrica == null) {
            mapMetrica = new HashMap<>();
            this.mapMetricas.put(chave, mapMetrica);
            for (MetricaMetaData metaData : this.cubo.getHierarquiaMetrica()) {
                Metrica metrica = metaData.createMetrica();
                mapMetrica.put(metaData.getTitulo(), metrica);
            }
        }
        return mapMetrica;
    }

    private String formaChave(Dimension dimensionLinha, Dimension dimensionColuna) {
        return dimensionLinha.getKeyValue() +
                "." +
                dimensionColuna.getKeyValue();
    }

    public void removeLinhaMetrica(Dimension dimensionLinha, Dimension dimensionColuna) {
        String chave = this.formaChave(dimensionLinha, dimensionColuna);
        this.mapMetricas.remove(chave);
    }

    public void removeLinhaMetrica(Dimension dimensionLinha) {
        String chave = this.formaChave(dimensionLinha, dimensionColunaNula);
        this.mapMetricas.remove(chave);
    }

}
