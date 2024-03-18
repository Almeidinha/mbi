package com.msoft.mbi.cube.multi.metrics.additive;

import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.column.TipoDecimal;
import com.msoft.mbi.cube.multi.column.TipoMetricaInteiro;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import lombok.Getter;


@Getter
public class MetricaAditivaMetaData extends MetricaMetaData {

    private String coluna = null;

    protected MetricaAditivaMetaData(String titulo, String coluna, DataType<Double> tipo) {
        super(titulo, tipo);
        this.coluna = coluna;
    }

    public static MetricaAditivaMetaData factory(CampoMetaData campoMetaData) {
        String titulo = campoMetaData.getTituloCampo();
        String coluna;
        if (campoMetaData.getApelidoCampo() != null) {
            coluna = campoMetaData.getApelidoCampo();
        } else {
            coluna = campoMetaData.getNomeCampo();
        }
        MetricaAditivaMetaData metricaMetaData;
        DataType<Double> dataType = null;
        if (CampoMetaData.TIPO_INTEIRO.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoMetricaInteiro();
        } else if (CampoMetaData.TIPO_DECIMAL.equals(campoMetaData.getTipoDado()) || CampoMetaData.TIPO_TEXTO.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoDecimal();
        }
        metricaMetaData = new MetricaAditivaMetaData(titulo, coluna, dataType);
        if (metricaMetaData.isExpressaoParcialLinhas()) {
            metricaMetaData.setTipoTotalizacaoLinhas(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
        } else {
            metricaMetaData.setTipoTotalizacaoLinhas(CampoMetaData.TOTALIZAR_APLICAR_SOMA);
        }
        MetricaMetaData.factory(metricaMetaData, campoMetaData);
        return metricaMetaData;
    }

    public void setColuna(String coluna) {
        this.coluna = coluna;
    }

    @Override
    public MetricaAditiva createMetrica() {
        MetricaAditiva metricaAditiva = new MetricaAditiva();
        metricaAditiva.setMetaData(this);
        metricaAditiva.setAgregador(this.agregacaoTipo);
        return metricaAditiva;
    }

}
