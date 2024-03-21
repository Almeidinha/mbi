package com.msoft.mbi.cube.multi.generation;

import java.util.Iterator;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoSomaTodasMetricas implements CalculationSummaryType {

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
    public Double calculate(Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, MetricMetaData metaData, String lineType) {
        Double valorImprimir = Double.valueOf(0);
        DimensionNullColumn dimensaoColunaNula = new DimensionNullColumn(dimension.getCube());
        int qtdDimensoesLin = 0;

        for (MetricMetaData metaDataAux : dimension.getCube().getMetricsTotalHorizontal()) {
            Double valor = metaDataAux.calculaValorTotalParcial(dimensionReferenceAxis, dimensaoColunaNula);
            valorImprimir += (valor != null ? valor : 0);
        }

        if (lineType.equals(CalculationSummaryType.MEDIA)) {
            if (dimensionReferenceAxis.toString().lastIndexOf("[") == 6) {
                Iterator<Dimension> it = dimensionReferenceAxis.getCube().getDimensionsLastLevelLines().iterator();

                while (it.hasNext()) {
                    Dimension dim = it.next();

                    if (dim.toString().contains(dimensionReferenceAxis.toString())) {
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
