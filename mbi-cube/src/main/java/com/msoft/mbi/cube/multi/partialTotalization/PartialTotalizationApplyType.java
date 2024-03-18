package com.msoft.mbi.cube.multi.partialTotalization;

import java.io.Serializable;

import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public interface PartialTotalizationApplyType extends Serializable {

    Double calculateValue(Dimension dimensionReferenceAxis, Dimension dimension, MetricMetaData metricMetaData, MetricsMap metricsMap);

}
