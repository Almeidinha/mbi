package com.msoft.mbi.cube.multi.generation;

import java.util.Iterator;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoMediaColuna implements CalculoSumarizacaoTipo {

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
    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricMetaData metaData, String tipoLinha) {
        DimensionColunaNula dimensaoColunaNula = new DimensionColunaNula(dimension.getCube());
        Double soma = metaData.calculaValorTotalParcial(dimensionEixoReferencia, dimensaoColunaNula);
        int qtdDimensoesCol = dimensionEixoReferencia.getCube().getDimensionsLastLevelColumns().size();
        int qtdDimensoesLin = 0;
        Double resultado;

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
