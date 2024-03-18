package com.msoft.mbi.cube.multi.partialTotalization;

import java.util.Map;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarTotal;

public class TotalizacaoParcialAplicarTipoExpressao implements TotalizacaoParcialAplicarTipo {


    private static TotalizacaoParcialAplicarTipoExpressao acumuladoParcialTipo;

    private TotalizacaoParcialAplicarTipoExpressao() {
        super();
    }

    public static TotalizacaoParcialAplicarTipoExpressao getInstance() {
        if (acumuladoParcialTipo == null) {
            acumuladoParcialTipo = new TotalizacaoParcialAplicarTipoExpressao();
        }
        return acumuladoParcialTipo;
    }

    @Override
    public Double calculaValor(Dimension dimensionEixoReferencia, Dimension dimension, MetricaMetaData metaData, MapaMetricas mapaMetricas) {
        if (!dimensionEixoReferencia.getMetaData().isLinha()) {
            Dimension dimAux = dimensionEixoReferencia;
            dimensionEixoReferencia = dimension;
            dimension = dimAux;
        }
        MetricLine metricLine = mapaMetricas.getMetricLine(dimensionEixoReferencia, dimension);
        Map<String, Metrica> metricas = metricLine.getMetrics();
        Metrica expressao = metricas.get(metaData.getTitulo());
        Double valor = expressao.calcula(mapaMetricas, metricLine, (MetricLine) null, MetricaValorUtilizarTotal.getInstance());
        return valor;
    }

}
