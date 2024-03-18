package com.msoft.mbi.cube.multi.metrics.calculated;

import java.io.Serial;
import java.util.List;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class MetricaCalculadaParticipacaoAHMetaData extends MetricaCalculadaParticipacaoMetaData {

    @Serial
    private static final long serialVersionUID = -4875827315659990838L;
    public static final String PARTICIPACAO_AH = "participacaoHorizontal";

    public MetricaCalculadaParticipacaoAHMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<AlertaCorMetaData> alertasCores) {
        super("AH % Participação", colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalizarLinhas(true);
    }

    @Override
    public String getFuncaoCampo() {
        return MetricaCalculadaParticipacaoAHMetaData.PARTICIPACAO_AH;
    }

    @Override
    public Dimension getDimensaoEixoReferencia(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionColuna();
    }

    @Override
    public Dimension getDimensaoOutra(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionLinha();
    }

}
