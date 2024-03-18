package com.msoft.mbi.cube.multi.partialTotalization;

import java.util.Iterator;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class TotalizacaoParcialAplicarTipoSoma implements TotalizacaoParcialAplicarTipo {

    private static TotalizacaoParcialAplicarTipoSoma acumuladoParcialTipo;

    private TotalizacaoParcialAplicarTipoSoma() {
        super();
    }

    public static TotalizacaoParcialAplicarTipoSoma getInstance() {
        if (acumuladoParcialTipo == null) {
            acumuladoParcialTipo = new TotalizacaoParcialAplicarTipoSoma();
        }
        return acumuladoParcialTipo;
    }

    public Double calculaValor(Dimension dimensionEixoReferencia, Dimension dimension, MetricaMetaData metaData, MapaMetricas mapaMetricas) {
        Double retorno = (double) 0;
        if (!dimensionEixoReferencia.getDimensoesAbaixo().isEmpty()) {
            Dimension dimensionAbaixo;
            Double valorDimensao;
            Iterator<Dimension> iDimensoesAbaixo = dimensionEixoReferencia.getDimensoesAbaixo().values().iterator();
            while (iDimensoesAbaixo.hasNext()) {
                dimensionAbaixo = iDimensoesAbaixo.next();
                valorDimensao = calculaValor(dimensionAbaixo, dimension, metaData, mapaMetricas);
                retorno += (valorDimensao != null ? valorDimensao : 0);
            }
        } else {
            if (!dimension.getDimensoesAbaixo().isEmpty()) {
                retorno = metaData.calculaValorTotalParcial(dimension, dimensionEixoReferencia);
            } else {
                Dimension dimensionLinha;
                Dimension dimensionColuna;
                if (dimensionEixoReferencia.getMetaData().isLinha()) {
                    dimensionLinha = dimensionEixoReferencia;
                    dimensionColuna = dimension;
                } else {
                    dimensionLinha = dimension;
                    dimensionColuna = dimensionEixoReferencia;
                }

                MetricLine metricLine = mapaMetricas.getMetricLine(dimensionLinha, dimensionColuna);
                Metrica metricaCalculada = metricLine.getMetrics().get(metaData.getTitulo());
                retorno = metricaCalculada.getValor(mapaMetricas, metricLine, null);
            }
        }
        return retorno;
    }
}
