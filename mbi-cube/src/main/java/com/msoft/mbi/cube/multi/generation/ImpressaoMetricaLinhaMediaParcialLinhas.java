package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaMediaParcialLinhas extends ImpressaoMetricaLinha {

    public ImpressaoMetricaLinhaMediaParcialLinhas(List<MetricMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoMediaLinha.getInstance(), MetricMetaData.MEDIA_PARTIAL);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Printer printer, Cube cube, String tipoLinha) {
        for (MetricMetaData metaData : this.metricas) {
            if (metaData.isMediaPartialLines()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, printer, cube, tipoLinha);
            } else {
                printer.printColumn(propriedadeCelula, printer.getNullValue());
            }
        }
    }

}
