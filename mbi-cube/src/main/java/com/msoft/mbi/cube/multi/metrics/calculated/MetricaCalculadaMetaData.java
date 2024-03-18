package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.column.TipoDecimal;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder.AgregacaoAplicarAntes;
import com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder.AgregacaoAplicarDepois;
import com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder.AgregacaoAplicarOrdem;
import lombok.Getter;


public class MetricaCalculadaMetaData extends MetricaMetaData {

    protected String expressao;
    @Getter
    private AgregacaoAplicarOrdem agregacaoAplicarOrdem;

    protected MetricaCalculadaMetaData(String titulo) {
        super(titulo, new TipoDecimal());
        this.agregacaoAplicarOrdem = AgregacaoAplicarAntes.getInstance();
    }

    public MetricaCalculadaMetaData(String titulo, String expressao) {
        this(titulo);
        this.expressao = expressao;
    }

    public static MetricaCalculadaMetaData factory(CampoMetaData campoMetaData) {
        String titulo = campoMetaData.getTituloCampo();
        String expressao = campoMetaData.getNomeCampo();
        MetricaCalculadaMetaData metricaMetaData = new MetricaCalculadaMetaData(titulo, expressao);
        MetricaMetaData.factory(metricaMetaData, campoMetaData);
        metricaMetaData.setTipoTotalizacaoLinhas(campoMetaData.getTipoTotalizacaoLinhas());
        metricaMetaData.setAgregacaoAplicarOrdem(campoMetaData.getAgregacaoAplicarOrdem());
        return metricaMetaData;
    }

    public Calculo createCalculo() {
        return new Calculo(this.expressao);
    }

    public void setAgregacaoAplicarOrdem(String agregacaoAplicarOrdem) {
        if (CampoMetaData.AGREGACAO_APLICAR_ANTES.equals(agregacaoAplicarOrdem)) {
            this.agregacaoAplicarOrdem = AgregacaoAplicarAntes.getInstance();
        } else {
            this.agregacaoAplicarOrdem = AgregacaoAplicarDepois.getInstance();
        }
    }

    @Override
    public MetricaCalculada createMetrica() {
        MetricaCalculada metricaCalculada = new MetricaCalculada();
        metricaCalculada.setMetaData(this);
        metricaCalculada.setAgregador(this.agregacaoTipo);
        return metricaCalculada;
    }

}
