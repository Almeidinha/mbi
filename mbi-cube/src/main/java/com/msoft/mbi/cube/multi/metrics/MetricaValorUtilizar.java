package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public interface MetricaValorUtilizar {

    public Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, String tituloMetrica);

    public Double calculaValor(Metrica expressao, MetricLine metricLine, MapaMetricas mapaMetricas);

    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo);
}
