package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAcumuladoValorAVMetaData extends MetricCalculatedAcumuladoMetaData {

    public static final String VALOR_ACUMULADO_AV = "acumuladoVertical";

    public MetricCalculatedAcumuladoValorAVMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<ColorAlertMetadata> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalPartialLines(false);
        this.setTotalLines(false);
    }

    @Override
    public Dimension getDimensaoEixoReferencia(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public Dimension getDimensaoOutra(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public String getFuncaoCampo() {
        return MetricCalculatedAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV;
    }
}
