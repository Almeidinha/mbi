package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.column.MaskColumnMetaData;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public abstract class MetricCalculatedAccParticipationMetaData extends MetricCalculatedAccMetaData {


    public MetricCalculatedAccParticipationMetaData(MetricMetaData referenceColumn, AnalysisParticipationType participationType,
                                                    List<ColorAlertMetadata> colorAlerts) {
        super(referenceColumn, participationType, colorAlerts);
        MaskColumnMetaData mascaraAH = new MaskColumnMetaData("%", MaskColumnMetaData.TYPE_AFTER);
        this.addDecorator(mascaraAH);
        this.setUsePercent(true);
    }

}
