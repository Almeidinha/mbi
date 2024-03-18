package com.msoft.mbi.cube.multi.metrics;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;

public class MetricaValorUtilizarTotal implements MetricaValorUtilizar {

    private static MetricaValorUtilizarTotal instance;
    private List<Dimension> dimensoesColuna;

    public static MetricaValorUtilizarTotal getInstance() {
        if (instance == null) {
            instance = new MetricaValorUtilizarTotal();
        }
        return instance;
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, String tituloCampo) {
        Cubo cubo = metricLine.getDimensionLine().getCube();
        MetricaMetaData metaData = cubo.getMetricaMetaDataByTitulo(tituloCampo);
        return metaData.calculaValorTotalParcial(metricLine.getDimensionLine(), new DimensionColunaNula(cubo));
    }

    @Override
    public Double calculaValor(Metrica metrica, MetricLine metricLine, MapaMetricas mapaMetricas) {
        return metrica.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
    }

    @Override
    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo) {
        if (dimensoesColuna == null) {
            dimensoesColuna = new ArrayList<Dimension>();
        }

        return dimensoesColuna;
    }

}
