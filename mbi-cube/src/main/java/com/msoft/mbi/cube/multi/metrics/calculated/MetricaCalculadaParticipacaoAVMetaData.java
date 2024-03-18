package com.msoft.mbi.cube.multi.metrics.calculated;

import java.io.Serial;
import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class MetricaCalculadaParticipacaoAVMetaData extends MetricaCalculadaParticipacaoMetaData {

    public static final String AV = "analiseVertical";

    public MetricaCalculadaParticipacaoAVMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<AlertaCorMetaData> alertasCores) {
        super("AV % Participação", colunaReferencia, analiseVerticalTipo, alertasCores);
    }

    @Override
    public String getFuncaoCampo() {
        return MetricaCalculadaParticipacaoAVMetaData.AV;
    }

    @Override
    public Dimension getDimensaoEixoReferencia(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public Dimension getDimensaoOutra(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

}
