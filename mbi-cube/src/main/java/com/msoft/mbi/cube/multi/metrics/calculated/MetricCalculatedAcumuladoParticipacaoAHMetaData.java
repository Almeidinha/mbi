package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedAcumuladoParticipacaoAHMetaData extends MetricCalculatedAcumuladoParticipacaoMetaData {

    public static final String PARTICIPACAO_ACUMULADA_AH = "participacaoAcumuladaHorizontal";

    public MetricCalculatedAcumuladoParticipacaoAHMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo,
                                                           List<ColorAlertMetadata> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalPartialLines(true);
        this.setTotalLinesType(MetaDataField.TOTAL_APPLY_EXPRESSION);
    }

    @Override
    public Dimension DimensionReferenceAxis(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public Dimension getDimensionOther(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH;
    }
}
