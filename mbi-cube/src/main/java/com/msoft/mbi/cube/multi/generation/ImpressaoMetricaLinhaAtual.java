package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaAtual extends ImpressaoMetricaLinha {

    public ImpressaoMetricaLinhaAtual(List<MetricMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoLinhaMetricaAtual.getInstance(), AlertaCorMetaData.SEM_FUNCAO);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cube cube, String tipoLinha) {
        for (MetricMetaData metaData : this.metricas) {
            this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, impressor, cube, tipoLinha);
        }
    }

}
