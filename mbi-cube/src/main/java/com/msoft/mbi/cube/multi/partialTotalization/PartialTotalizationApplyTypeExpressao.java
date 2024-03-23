package com.msoft.mbi.cube.multi.partialTotalization;

import java.util.Map;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricValueUseTotal;

public class PartialTotalizationApplyTypeExpressao implements PartialTotalizationApplyType {


    private static PartialTotalizationApplyTypeExpressao acumuladoParcialTipo;

    private PartialTotalizationApplyTypeExpressao() {
        super();
    }

    public static PartialTotalizationApplyTypeExpressao getInstance() {
        if (acumuladoParcialTipo == null) {
            acumuladoParcialTipo = new PartialTotalizationApplyTypeExpressao();
        }
        return acumuladoParcialTipo;
    }

    @Override
    public Double calculateValue(Dimension dimensionReferenceAxis, Dimension dimension, MetricMetaData metricMetaData, MetricsMap metricsMap) {
        if (!dimensionReferenceAxis.getMetaData().isLine()) {
            Dimension dimAux = dimensionReferenceAxis;
            dimensionReferenceAxis = dimension;
            dimension = dimAux;
        }
        MetricLine metricLine = metricsMap.getMetricLine(dimensionReferenceAxis, dimension);
        Map<String, Metric> metricas = metricLine.getMetrics();
        Metric expressao = metricas.get(metricMetaData.getTitle());
        Double valor = expressao.calculate(metricsMap, metricLine, (MetricLine) null, MetricValueUseTotal.getInstance());
        return valor;
    }

}
