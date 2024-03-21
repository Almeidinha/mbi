package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public interface CalculationSummaryType {


    String NORMAL = "normal";
    String MEDIA = "media";
    String TOTAL = "total";

    Double calculate(Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, MetricMetaData metaData, String lineType);

}
