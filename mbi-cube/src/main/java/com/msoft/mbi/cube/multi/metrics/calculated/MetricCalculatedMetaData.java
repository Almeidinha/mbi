package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.column.TipoDecimal;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder.AgregacaoAplicarAntes;
import com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder.AgregacaoAplicarDepois;
import com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder.AggregationApplyOrder;
import lombok.Getter;


public class MetricCalculatedMetaData extends MetricMetaData {

    protected String expression;
    @Getter
    private AggregationApplyOrder aggregationApplyOrder;

    protected MetricCalculatedMetaData(String title) {
        super(title, new TipoDecimal());
        this.aggregationApplyOrder = AgregacaoAplicarAntes.getInstance();
    }

    public MetricCalculatedMetaData(String title, String expression) {
        this(title);
        this.expression = expression;
    }

    public static MetricCalculatedMetaData factory(CampoMetaData campoMetaData) {
        String title = campoMetaData.getTituloCampo();
        String expression = campoMetaData.getNomeCampo();
        MetricCalculatedMetaData metricMetadata = new MetricCalculatedMetaData(title, expression);
        MetricMetaData.factory(metricMetadata, campoMetaData);
        metricMetadata.setTotalLinesType(campoMetaData.getTipoTotalizacaoLinhas());
        metricMetadata.setAggregationApplyOrder(campoMetaData.getAgregacaoAplicarOrdem());
        return metricMetadata;
    }

    public Calculo createCalculo() {
        return new Calculo(this.expression);
    }

    public void setAggregationApplyOrder(String aggregationOrder) {
        if (CampoMetaData.AGREGACAO_APLICAR_ANTES.equals(aggregationOrder)) {
            this.aggregationApplyOrder = AgregacaoAplicarAntes.getInstance();
        } else {
            this.aggregationApplyOrder = AgregacaoAplicarDepois.getInstance();
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
