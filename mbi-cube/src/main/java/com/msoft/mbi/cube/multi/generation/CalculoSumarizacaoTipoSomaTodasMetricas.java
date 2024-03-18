package com.msoft.mbi.cube.multi.generation;

import java.util.Iterator;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class CalculoSumarizacaoTipoSomaTodasMetricas implements CalculoSumarizacaoTipo {

    private static CalculoSumarizacaoTipoSomaTodasMetricas calculo;

    private CalculoSumarizacaoTipoSomaTodasMetricas() {
        super();
    }

    public static CalculoSumarizacaoTipoSomaTodasMetricas getInstance() {
        if (calculo == null) {
            calculo = new CalculoSumarizacaoTipoSomaTodasMetricas();
        }
        return calculo;
    }

    @Override
    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricaMetaData metaData, String tipoLinha) {
        Double valorImprimir = Double.valueOf(0);
        DimensionColunaNula dimensaoColunaNula = new DimensionColunaNula(dimension.getCube());
        int qtdDimensoesLin = 0;

        for (MetricaMetaData metaDataAux : dimension.getCube().getMetricasTotalizacaoHorizontal()) {
            Double valor = metaDataAux.calculaValorTotalParcial(dimensionEixoReferencia, dimensaoColunaNula);
            valorImprimir += (valor != null ? valor : 0);
        }

        if (tipoLinha.equals(CalculoSumarizacaoTipo.MEDIA)) {
            if (dimensionEixoReferencia.toString().lastIndexOf("[") == 6) {
                Iterator<Dimension> it = dimensionEixoReferencia.getCube().getDimensoesUltimoNivelLinha().iterator();

                while (it.hasNext()) {
                    Dimension dim = it.next();

                    if (dim.toString().contains(dimensionEixoReferencia.toString())) {
                        qtdDimensoesLin++;
                    }
                }
            }

            if (qtdDimensoesLin > 0) {
                valorImprimir = valorImprimir / qtdDimensoesLin;
            }
        }

        return valorImprimir;
    }
}
