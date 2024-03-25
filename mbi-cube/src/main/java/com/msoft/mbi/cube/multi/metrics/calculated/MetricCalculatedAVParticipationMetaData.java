package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAVParticipationMetaData extends MetricCalculatedParticipacaoMetaData {

    public static final String AV = "analiseVertical";

    public MetricCalculatedAVParticipationMetaData(MetricMetaData referenceColumn, AnalysisParticipationType verticalAnalysisType, List<ColorAlertMetadata> colorAlerts) {
        super("AV % Participação", referenceColumn, verticalAnalysisType, colorAlerts);
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedAVParticipationMetaData.AV;
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
