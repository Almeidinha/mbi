package com.msoft.mbi.cube.multi.metrics;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.LinhaMetrica;
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
    public Double getValor(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, String tituloCampo) {
        Cubo cubo = linhaMetrica.getDimensionLinha().getCube();
        MetricaMetaData metaData = cubo.getMetricaMetaDataByTitulo(tituloCampo);
        return metaData.calculaValorTotalParcial(linhaMetrica.getDimensionLinha(), new DimensionColunaNula(cubo));
    }

    @Override
    public Double calculaValor(Metrica metrica, LinhaMetrica linhaMetrica, MapaMetricas mapaMetricas) {
        return metrica.getMetaData().calculaValorTotalParcial(linhaMetrica.getDimensionLinha(), linhaMetrica.getDimensionColuna());
    }

    @Override
    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo) {
        if (dimensoesColuna == null) {
            dimensoesColuna = new ArrayList<Dimension>();
        }

        return dimensoesColuna;
    }

}
