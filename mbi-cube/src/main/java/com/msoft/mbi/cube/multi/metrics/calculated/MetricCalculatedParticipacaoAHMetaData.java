package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedParticipacaoAHMetaData extends MetricCalculatedParticipacaoMetaData {

    public static final String PARTICIPACAO_AH = "participacaoHorizontal";

    public MetricCalculatedParticipacaoAHMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<ColorAlertMetadata> alertasCores) {
        super("AH % Participação", colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalLines(true);
    }

    @Override
    public String getFuncaoCampo() {
        return MetricCalculatedParticipacaoAHMetaData.PARTICIPACAO_AH;
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
