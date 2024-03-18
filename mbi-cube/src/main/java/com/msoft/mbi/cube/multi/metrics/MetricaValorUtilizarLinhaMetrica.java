package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public class MetricaValorUtilizarLinhaMetrica implements MetricaValorUtilizar {

    private static MetricaValorUtilizarLinhaMetrica instance;

    public static MetricaValorUtilizarLinhaMetrica getInstance() {
        if (instance == null) {
            instance = new MetricaValorUtilizarLinhaMetrica();
        }
        return instance;
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, String tituloCampo) {
        Metrica expressao = metricLine.getMetrics().get(tituloCampo);
        return expressao.getValor(mapaMetricas, metricLine, (MetricLine) null);
    }

    @Override
    public Double calculaValor(Metrica metrica, MetricLine metricLine, MapaMetricas mapaMetricas) {
        return metrica.calcula(mapaMetricas, metricLine, (MetricLine) null);
    }

    @Override
    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo) {
        return cubo.getDimensoesUltimoNivelColuna();
    }

}
