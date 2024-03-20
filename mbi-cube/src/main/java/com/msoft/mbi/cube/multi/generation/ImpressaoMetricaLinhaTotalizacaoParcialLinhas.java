package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaTotalizacaoParcialLinhas extends ImpressaoMetricaLinha {

    public ImpressaoMetricaLinhaTotalizacaoParcialLinhas(List<MetricMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricMetaData.TOTAL_PARTIAL);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Printer printer, Cube cube, String tipoLinha) {
        for (MetricMetaData metaData : this.metricas) {
            if (metaData.isTotalPartialLines()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, null, dimensionColuna, printer, cube, tipoLinha);
            } else if (metaData.isPartialTotalExpression()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, null, dimensionColuna, printer, cube, CalculoSumarizacaoTipoExpressao.getInstance(), tipoLinha);
            } else {
                printer.printColumn(propriedadeCelula, printer.getNullValue());
            }
        }
    }

}
