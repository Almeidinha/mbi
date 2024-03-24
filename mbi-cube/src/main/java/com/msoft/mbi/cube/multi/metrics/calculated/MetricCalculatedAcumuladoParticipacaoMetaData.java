package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.column.MaskColumnMetaData;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public abstract class MetricCalculatedAcumuladoParticipacaoMetaData extends MetricCalculatedAcumuladoMetaData {


    public MetricCalculatedAcumuladoParticipacaoMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo,
                                                         List<ColorAlertMetadata> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        MaskColumnMetaData mascaraAH = new MaskColumnMetaData("%", MaskColumnMetaData.TYPE_AFTER);
        this.addDecorator(mascaraAH);
        this.setUsePercent(true);
    }

}
