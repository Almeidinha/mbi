package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class MetricaCalculadaAcumuladoParticipacaoAHMetaData extends MetricaCalculadaAcumuladoParticipacaoMetaData {

    private static final long serialVersionUID = -4419177231706423003L;
    public static final String PARTICIPACAO_ACUMULADA_AH = "participacaoAcumuladaHorizontal";

    public MetricaCalculadaAcumuladoParticipacaoAHMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo,
                                                           List<AlertaCorMetaData> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalizarParcialLinhas(true);
        this.setTipoTotalizacaoLinhas(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
    }

    @Override
    public Dimension getDimensaoEixoReferencia(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionColuna();
    }

    @Override
    public Dimension getDimensaoOutra(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionLinha();
    }

    @Override
    public String getFuncaoCampo() {
        return MetricaCalculadaAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH;
    }
}
