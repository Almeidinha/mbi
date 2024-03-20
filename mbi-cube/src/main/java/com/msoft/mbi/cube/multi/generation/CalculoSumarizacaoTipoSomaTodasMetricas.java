package com.msoft.mbi.cube.multi.generation;

import java.util.Iterator;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

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
    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricMetaData metaData, String tipoLinha) {
        Double valorImprimir = Double.valueOf(0);
        DimensionNullColumn dimensaoColunaNula = new DimensionNullColumn(dimension.getCube());
        int qtdDimensoesLin = 0;

        for (MetricMetaData metaDataAux : dimension.getCube().getMetricsTotalHorizontal()) {
            Double valor = metaDataAux.calculaValorTotalParcial(dimensionEixoReferencia, dimensaoColunaNula);
            valorImprimir += (valor != null ? valor : 0);
        }

        if (tipoLinha.equals(CalculoSumarizacaoTipo.MEDIA)) {
            if (dimensionEixoReferencia.toString().lastIndexOf("[") == 6) {
                Iterator<Dimension> it = dimensionEixoReferencia.getCube().getDimensionsLastLevelLines().iterator();

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
