package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaExpressaoParcialLinhas extends ImpressaoMetricaLinha {

    public ImpressaoMetricaLinhaExpressaoParcialLinhas(List<MetricMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoExpressao.getInstance(), MetricMetaData.EXPRESSION_PARTIAL);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Printer printer, Cube cube, String tipoLinha) {
        for (MetricMetaData metaData : this.metricas) {
            if (metaData.isExpressionPartialLines()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, printer, cube, tipoLinha);
            } else {
                printer.printColumn(propriedadeCelula, printer.getNullValue());
            }
        }
    }


}
