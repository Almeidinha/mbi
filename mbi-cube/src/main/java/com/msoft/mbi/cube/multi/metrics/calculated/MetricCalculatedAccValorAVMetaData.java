package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAccValorAVMetaData extends MetricCalculatedAccMetaData {

    public static final String ACCUMULATED_VALUE_AV = "acumuladoVertical";

    public MetricCalculatedAccValorAVMetaData(MetricMetaData referenceColumn, AnalysisParticipationType participationType, List<ColorAlertMetadata> colorsAlert) {
        super(referenceColumn, participationType, colorsAlert);
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
        return MetricCalculatedAccValorAVMetaData.ACCUMULATED_VALUE_AV;
    }
}
