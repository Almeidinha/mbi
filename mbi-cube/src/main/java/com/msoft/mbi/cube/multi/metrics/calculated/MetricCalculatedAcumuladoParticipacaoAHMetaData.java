package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAcumuladoParticipacaoAHMetaData extends MetricCalculatedAcumuladoParticipacaoMetaData {

    public static final String PARTICIPACAO_ACUMULADA_AH = "participacaoAcumuladaHorizontal";

    public MetricCalculatedAcumuladoParticipacaoAHMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo,
                                                           List<AlertaCorMetaData> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalPartialLines(true);
        this.setTotalLinesType(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
    }

    @Override
    public Dimension getDimensaoEixoReferencia(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public Dimension getDimensaoOutra(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public String getFuncaoCampo() {
        return MetricCalculatedAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH;
    }
}
