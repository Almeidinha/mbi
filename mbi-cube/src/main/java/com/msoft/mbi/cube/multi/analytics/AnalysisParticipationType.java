package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public interface AnalysisParticipationType {

    Dimension getUpperLevelDimension(Dimension currentDimension);
}
