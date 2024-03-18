package com.msoft.mbi.cube.multi.partialTotalization;

import java.io.Serial;
import java.util.Iterator;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class TotalizacaoParcialAplicarTipoMedia implements TotalizacaoParcialAplicarTipo {

    @Serial
    private static final long serialVersionUID = -6349511106830024244L;

    private static TotalizacaoParcialAplicarTipoMedia acumuladoParcialTipo;

    private TotalizacaoParcialAplicarTipoMedia() {
        super();
    }

    public static TotalizacaoParcialAplicarTipoMedia getInstance() {
        if (acumuladoParcialTipo == null) {
            acumuladoParcialTipo = new TotalizacaoParcialAplicarTipoMedia();
        }
        return acumuladoParcialTipo;
    }

    public Double calculaValor(Dimension dimensionEixoReferencia, Dimension dimension, MetricaMetaData metaData, MapaMetricas mapaMetricas) {
        Double retorno = (double) 0;
        if (!dimensionEixoReferencia.getDimensoesAbaixo().isEmpty()) {
            Dimension dimensionAbaixo;
            Double valorDimensao;
            Iterator<Dimension> iDimensoesAbaixo = dimensionEixoReferencia.getDimensoesAbaixo().values().iterator();
            int count = 0;
            while (iDimensoesAbaixo.hasNext()) {
                dimensionAbaixo = iDimensoesAbaixo.next();
                valorDimensao = calculaValor(dimensionAbaixo, dimension, metaData, mapaMetricas);
                retorno += (valorDimensao != null ? valorDimensao : 0);
                count++;
            }
            retorno = retorno / count;
        } else {
            if (!dimension.getDimensoesAbaixo().isEmpty()) {
                retorno = metaData.calculaValorTotalParcial(dimension, dimensionEixoReferencia);
            } else {
                Dimension dimensionLinha = null;
                Dimension dimensionColuna = null;
                if (dimensionEixoReferencia.getMetaData().isLinha()) {
                    dimensionLinha = dimensionEixoReferencia;
                    dimensionColuna = dimension;
                } else {
                    dimensionLinha = dimension;
                    dimensionColuna = dimensionEixoReferencia;
                }

                LinhaMetrica linhaMetrica = mapaMetricas.getLinhaMetrica(dimensionLinha, dimensionColuna);
                Metrica metricaCalculada = linhaMetrica.getMetricas().get(metaData.getTitulo());
                retorno = metricaCalculada.getValor(mapaMetricas, linhaMetrica, null);
            }
        }
        return retorno;
    }

}
