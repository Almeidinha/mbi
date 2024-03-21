package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public interface MetricCalculatedFunctionMetaData {

    String getFieldFunction();

    String getReferenceFieldTitle();

    AnaliseParticipacaoTipo getParticipationAnalysisType();

    Dimension DimensionReferenceAxis(MetricLine metricLine);

    Dimension getDimensionOther(MetricLine metricLine);

}
