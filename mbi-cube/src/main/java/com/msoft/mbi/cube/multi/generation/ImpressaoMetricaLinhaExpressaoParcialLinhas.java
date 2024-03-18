package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ImpressaoMetricaLinhaExpressaoParcialLinhas extends ImpressaoMetricaLinha {

    public ImpressaoMetricaLinhaExpressaoParcialLinhas(List<MetricaMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoExpressao.getInstance(), MetricaMetaData.EXPRESSAO_PARCIAL);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cubo cubo, String tipoLinha) {
        for (MetricaMetaData metaData : this.metricas) {
            if (metaData.isExpressaoParcialLinhas()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, impressor, cubo, tipoLinha);
            } else {
                impressor.imprimeColuna(propriedadeCelula, impressor.getValorNulo());
            }
        }
    }


}
