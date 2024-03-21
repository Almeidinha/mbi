package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAcumuladoParticipacaoAVMetaData extends MetricCalculatedAcumuladoParticipacaoMetaData {

    public static final String PARTICIPACAO_ACUMULADA_AV = "participacaoAcumulada";

    public MetricCalculatedAcumuladoParticipacaoAVMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<ColorAlertMetadata> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalPartialLines(false);
        this.setTotalLines(false);
    }

    @Override
    public Dimension DimensionReferenceAxis(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public Dimension getDimensionOther(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV;
    }
}
