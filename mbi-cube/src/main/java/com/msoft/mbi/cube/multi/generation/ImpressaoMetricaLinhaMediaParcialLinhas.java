package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ImpressaoMetricaLinhaMediaParcialLinhas extends ImpressaoMetricaLinha {

    public ImpressaoMetricaLinhaMediaParcialLinhas(List<MetricaMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoMediaLinha.getInstance(), MetricaMetaData.MEDIA_PARCIAL);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cubo cubo, String tipoLinha) {
        for (MetricaMetaData metaData : this.metricas) {
            if (metaData.isMediaParcialLinhas()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, impressor, cubo, tipoLinha);
            } else {
                impressor.imprimeColuna(propriedadeCelula, impressor.getValorNulo());
            }
        }
    }

}
