package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedParticipacaoAVMetaData extends MetricCalculatedParticipacaoMetaData {

    public static final String AV = "analiseVertical";

    public MetricCalculatedParticipacaoAVMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<ColorAlertMetadata> alertasCores) {
        super("AV % Participação", colunaReferencia, analiseVerticalTipo, alertasCores);
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedParticipacaoAVMetaData.AV;
    }

    @Override
    public Dimension DimensionReferenceAxis(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public Dimension getDimensionOther(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

}
