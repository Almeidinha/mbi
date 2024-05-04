package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNull;

public class AnalysisParticipationTypeGeneral implements AnalysisParticipationType {

    private static AnalysisParticipationTypeGeneral analysisParticipationTypeGeneral;

    private AnalysisParticipationTypeGeneral() {
        super();
    }

    public static AnalysisParticipationTypeGeneral getInstance() {
        if (analysisParticipationTypeGeneral == null) {
            analysisParticipationTypeGeneral = new AnalysisParticipationTypeGeneral();
        }
        return analysisParticipationTypeGeneral;
    }

    @Override
    public Dimension getUpperLevelDimension(Dimension currentDimension) {
        return DimensionNull.createDimensionNull(currentDimension.getMetaData().getReferenceAxis(), currentDimension.getCube());
    }
}
