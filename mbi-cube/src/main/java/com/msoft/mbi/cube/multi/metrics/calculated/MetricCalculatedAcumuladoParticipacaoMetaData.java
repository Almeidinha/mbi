package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public abstract class MetricCalculatedAcumuladoParticipacaoMetaData extends MetricCalculatedAcumuladoMetaData {


    public MetricCalculatedAcumuladoParticipacaoMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo,
                                                         List<ColorAlertMetadata> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        MascaraColunaMetaData mascaraAH = new MascaraColunaMetaData("%", MascaraColunaMetaData.TIPO_DEPOIS);
        this.addDecorator(mascaraAH);
        this.setUsePercent(true);
    }

}
