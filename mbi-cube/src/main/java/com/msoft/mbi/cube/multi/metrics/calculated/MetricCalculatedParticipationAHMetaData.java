package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedParticipationAHMetaData extends MetricCalculatedParticipationMetaData {

    public static final String PARTICIPACAO_AH = "participacaoHorizontal";

    public MetricCalculatedParticipationAHMetaData(MetricMetaData referenceColumn, AnalysisParticipationType participationType, List<ColorAlertMetadata> colorAlerts) {
        super("AH % Participação", referenceColumn, participationType, colorAlerts);
        this.setTotalLines(true);
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedParticipationAHMetaData.PARTICIPACAO_AH;
    }

    @Override
    public Dimension DimensionReferenceAxis(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public Dimension getDimensionOther(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

}
