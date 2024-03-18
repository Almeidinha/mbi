package com.msoft.mbi.cube.multi.metrics.calculated;

import java.io.Serial;
import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class MetricaCalculadaParticipacaoAHMetaData extends MetricaCalculadaParticipacaoMetaData {

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
    public Dimension getDimensaoEixoReferencia(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public Dimension getDimensaoOutra(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

}
