package com.msoft.mbi.cube.multi.metrics.additive;

import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.column.TipoDecimal;
import com.msoft.mbi.cube.multi.column.TipoMetricaInteiro;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
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

    public static MetricAdditiveMetaData factory(CampoMetaData campoMetaData) {
        String title = campoMetaData.getTituloCampo();
        String column;
        if (campoMetaData.getApelidoCampo() != null) {
            column = campoMetaData.getApelidoCampo();
        } else {
            column = campoMetaData.getNomeCampo();
        }
        MetricAdditiveMetaData additiveMetaData;
        DataType<Double> dataType = null;
        if (CampoMetaData.TIPO_INTEIRO.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoMetricaInteiro();
        } else if (CampoMetaData.TIPO_DECIMAL.equals(campoMetaData.getTipoDado()) || CampoMetaData.TIPO_TEXTO.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoDecimal();
        }
        additiveMetaData = new MetricAdditiveMetaData(title, column, dataType);
        if (additiveMetaData.isExpressionPartialLines()) {
            additiveMetaData.setTotalLinesType(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
        } else {
            additiveMetaData.setTotalLinesType(CampoMetaData.TOTALIZAR_APLICAR_SOMA);
        }
        MetricMetaData.factory(additiveMetaData, campoMetaData);
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
