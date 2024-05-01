package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAccParticipationAHMetaData extends MetricCalculatedAccParticipationMetaData {

    public static final String ACC_PARTICIPATION_AH = "participacaoAcumuladaHorizontal";

    public MetricCalculatedAccParticipationAHMetaData(MetricMetaData referenceColumn, AnalysisParticipationType participationType,
                                                      List<ColorAlertMetadata> colorAlerts) {
        super(referenceColumn, participationType, colorAlerts);
        this.setTotalPartialLines(true);
        this.setTotalLinesType(MetaDataField.TOTAL_APPLY_EXPRESSION);
    }

    @Override
    public Dimension DimensionReferenceAxis(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public Dimension getDimensionOther(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedAccParticipationAHMetaData.ACC_PARTICIPATION_AH;
    }
}
