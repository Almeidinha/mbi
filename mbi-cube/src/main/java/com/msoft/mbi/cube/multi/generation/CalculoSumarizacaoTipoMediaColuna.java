package com.msoft.mbi.cube.multi.generation;

import java.util.Iterator;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoMediaColuna implements CalculationSummaryType {

    private static CalculoSumarizacaoTipoMediaColuna calculo;

    private CalculoSumarizacaoTipoMediaColuna() {
        super();
    }

    public static CalculoSumarizacaoTipoMediaColuna getInstance() {
        if (calculo == null) {
            calculo = new CalculoSumarizacaoTipoMediaColuna();
        }
        return calculo;
    }

    @Override
    public Double calculate(Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, MetricMetaData metaData, String lineType) {
        DimensionNullColumn dimensaoColunaNula = new DimensionNullColumn(dimension.getCube());
        Double soma = metaData.calculaValorTotalParcial(dimensionReferenceAxis, dimensaoColunaNula);
        int qtdDimensoesCol = dimensionReferenceAxis.getCube().getDimensionsLastLevelColumns().size();
        int qtdDimensoesLin = 0;
        Double resultado;

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
        }

        if (soma == null) {
            return Double.valueOf(0);
        } else {
            resultado = soma / (qtdDimensoesCol != 0 ? qtdDimensoesCol : 1);

            if (qtdDimensoesLin > 0) {
                resultado = resultado / qtdDimensoesLin;
            }

            return resultado;
        }
    }

}
