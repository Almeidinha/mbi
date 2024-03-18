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
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cube cube, String tipoLinha) {
        for (MetricMetaData metaData : this.metricas) {
            if (metaData.isTotalPartialLines()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, null, dimensionColuna, impressor, cube, tipoLinha);
            } else if (metaData.isPartialTotalExpression()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, null, dimensionColuna, impressor, cube, CalculoSumarizacaoTipoExpressao.getInstance(), tipoLinha);
            } else {
                impressor.imprimeColuna(propriedadeCelula, impressor.getValorNulo());
            }
        }
    }

}
