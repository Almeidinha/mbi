package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.LinhaMetrica;
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
    public Double getValor(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, String tituloCampo) {
        Metrica expressao = linhaMetrica.getMetricas().get(tituloCampo);
        return expressao.getValor(mapaMetricas, linhaMetrica, (LinhaMetrica) null);
    }

    @Override
    public Double calculaValor(Metrica metrica, LinhaMetrica linhaMetrica, MapaMetricas mapaMetricas) {
        return metrica.calcula(mapaMetricas, linhaMetrica, (LinhaMetrica) null);
    }

    @Override
    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo) {
        return cubo.getDimensoesUltimoNivelColuna();
    }

}
