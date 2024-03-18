package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ImpressaoMetricaLinhaTotalizacaoParcialLinhas extends ImpressaoMetricaLinha {

    public ImpressaoMetricaLinhaTotalizacaoParcialLinhas(List<MetricaMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricaMetaData.TOTALIZACAO_PARCIAL);
    }

    public static final String FWJ_VERSAO = "$Revision: 1.3 $";

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cubo cubo, String tipoLinha) {
        for (MetricaMetaData metaData : this.metricas) {
            if (metaData.isTotalizarParcialLinhas()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, null, dimensionColuna, impressor, cubo, tipoLinha);
            } else if (metaData.isExpressaoParcialTotal()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, null, dimensionColuna, impressor, cubo, CalculoSumarizacaoTipoExpressao.getInstance(), tipoLinha);
            } else {
                impressor.imprimeColuna(propriedadeCelula, impressor.getValorNulo());
            }
        }
    }

}
