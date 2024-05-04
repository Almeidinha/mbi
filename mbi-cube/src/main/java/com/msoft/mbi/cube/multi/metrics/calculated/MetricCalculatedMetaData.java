package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.column.TypeDecimal;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.applyaggregationorder.AggregationApplyBefore;
import com.msoft.mbi.cube.multi.metrics.calculated.applyaggregationorder.AggregationApplyAfter;
import com.msoft.mbi.cube.multi.metrics.calculated.applyaggregationorder.AggregationApplyOrder;
import lombok.Getter;


public class MetricCalculatedMetaData extends MetricMetaData {

    protected String expression;
    @Getter
    private AggregationApplyOrder aggregationApplyOrder;

    protected MetricCalculatedMetaData(String title) {
        super(title, new TypeDecimal());
        this.aggregationApplyOrder = AggregationApplyBefore.getInstance();
    }

    public MetricCalculatedMetaData(String title, String expression) {
        this(title);
        this.expression = expression;
    }

    public static MetricCalculatedMetaData factory(MetaDataField metaDataField) {
        String title = metaDataField.getTitle();
        String expression = metaDataField.getName();
        MetricCalculatedMetaData metricMetadata = new MetricCalculatedMetaData(title, expression);
        MetricMetaData.factory(metricMetadata, metaDataField);
        metricMetadata.setTotalLinesType(metaDataField.getTotalLinesType());
        metricMetadata.setAggregationApplyOrder(metaDataField.getAggregationApplyOrder());
        return metricMetadata;
    }

    public Calculation createCalculation() {
        return new Calculation(this.expression);
    }

    public void setAggregationApplyOrder(String aggregationOrder) {
        if (MetaDataField.AGGREGATION_APPLY_BEFORE.equals(aggregationOrder)) {
            this.aggregationApplyOrder = AggregationApplyBefore.getInstance();
        } else {
            this.aggregationApplyOrder = AggregationApplyAfter.getInstance();
        }
    }

    @Override
    public MetricCalculated createMetrica() {
        MetricCalculated metricCalculated = new MetricCalculated();
        metricCalculated.setMetaData(this);
        metricCalculated.setAggregator(this.aggregationType);
        return metricCalculated;
    }

}
