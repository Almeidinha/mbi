package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class MetricaCalculadaAcumuladoValorAVMetaData extends MetricaCalculadaAcumuladoMetaData {

    private static final long serialVersionUID = -183988575823919780L;
    public static final String VALOR_ACUMULADO_AV = "acumuladoVertical";

    public MetricaCalculadaAcumuladoValorAVMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<AlertaCorMetaData> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalizarParcialLinhas(false);
        this.setTotalizarLinhas(false);
    }

    @Override
    public Dimension getDimensaoEixoReferencia(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionLinha();
    }

    @Override
    public Dimension getDimensaoOutra(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionColuna();
    }

    @Override
    public String getFuncaoCampo() {
        return MetricaCalculadaAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV;
    }
}
