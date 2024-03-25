package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAccParticipationAVMetaData extends MetricCalculatedAccParticipationMetaData {

    public static final String ACC_PARTICIPATION_AV = "participacaoAcumulada";

    public MetricCalculatedAccParticipationAVMetaData(MetricMetaData reference_column, AnalysisParticipationType participationType, List<ColorAlertMetadata> alertColors) {
        super(reference_column, participationType, alertColors);
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
        return MetricCalculatedAccParticipationAVMetaData.ACC_PARTICIPATION_AV;
    }
}
