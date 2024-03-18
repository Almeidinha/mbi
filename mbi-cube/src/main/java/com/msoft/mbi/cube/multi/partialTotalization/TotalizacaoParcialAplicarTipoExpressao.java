package com.msoft.mbi.cube.multi.partialTotalization;

import java.io.Serial;
import java.util.Map;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarTotal;

public class TotalizacaoParcialAplicarTipoExpressao implements TotalizacaoParcialAplicarTipo {

    @Serial
    private static final long serialVersionUID = 7830651675746489789L;

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
        LinhaMetrica linhaMetrica = mapaMetricas.getLinhaMetrica(dimensionEixoReferencia, dimension);
        Map<String, Metrica> metricas = linhaMetrica.getMetricas();
        Metrica expressao = metricas.get(metaData.getTitulo());
        Double valor = expressao.calcula(mapaMetricas, linhaMetrica, (LinhaMetrica) null, MetricaValorUtilizarTotal.getInstance());
        return valor;
    }

}
