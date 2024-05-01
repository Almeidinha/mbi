package com.msoft.mbi.cube.multi.metrics.additive;

import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.column.TypeDecimal;
import com.msoft.mbi.cube.multi.column.TypeMetricInt;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class MetricAdditiveMetaData extends MetricMetaData {

    private String column;

    protected MetricAdditiveMetaData(String title, String column, DataType<Double> type) {
        super(title, type);
        this.column = column;
    }

    public static MetricAdditiveMetaData factory(MetaDataField metaDataField) {
        String title = metaDataField.getTitle();
        String column;
        if (metaDataField.getFieldNickname() != null) {
            column = metaDataField.getFieldNickname();
        } else {
            column = metaDataField.getName();
        }
        MetricAdditiveMetaData additiveMetaData;
        DataType<Double> dataType = null;
        if (MetaDataField.INT_TYPE.equals(metaDataField.getDataType())) {
            dataType = new TypeMetricInt();
        } else if (MetaDataField.DECIMAL_TYPE.equals(metaDataField.getDataType()) || MetaDataField.TEST_TYPE.equals(metaDataField.getDataType())) {
            dataType = new TypeDecimal();
        }
        additiveMetaData = new MetricAdditiveMetaData(title, column, dataType);
        if (additiveMetaData.isExpressionPartialLines()) {
            additiveMetaData.setTotalLinesType(MetaDataField.TOTAL_APPLY_EXPRESSION);
        } else {
            additiveMetaData.setTotalLinesType(MetaDataField.TOTAL_APPLY_SUM);
        }
        MetricMetaData.factory(additiveMetaData, metaDataField);
        return additiveMetaData;
    }

    @Override
    public MetricAditiva createMetrica() {
        MetricAditiva metricaAditiva = new MetricAditiva();
        metricaAditiva.setMetaData(this);
        metricaAditiva.setAggregator(this.aggregationType);
        return metricaAditiva;
    }

}
