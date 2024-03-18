package com.msoft.mbi.cube.multi.metrics.calculated;

import java.io.Serial;
import java.util.List;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class MetricaCalculadaParticipacaoAVMetaData extends MetricaCalculadaParticipacaoMetaData {

    @Serial
    private static final long serialVersionUID = 1454250344315246512L;
    public static final String AV = "analiseVertical";

    public MetricaCalculadaParticipacaoAVMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<AlertaCorMetaData> alertasCores) {
        super("AV % Participação", colunaReferencia, analiseVerticalTipo, alertasCores);
    }

    @Override
    public String getFuncaoCampo() {
        return MetricaCalculadaParticipacaoAVMetaData.AV;
    }

    @Override
    public Dimension getDimensaoEixoReferencia(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionLinha();
    }

    @Override
    public Dimension getDimensaoOutra(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionColuna();
    }

}
