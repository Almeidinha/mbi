package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public abstract class MetricaCalculadaAcumuladoParticipacaoMetaData extends MetricaCalculadaAcumuladoMetaData {

    private static final long serialVersionUID = -3529627922537821443L;

    public MetricaCalculadaAcumuladoParticipacaoMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo,
                                                         List<AlertaCorMetaData> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        MascaraColunaMetaData mascaraAH = new MascaraColunaMetaData("%", MascaraColunaMetaData.TIPO_DEPOIS);
        this.addDecorator(mascaraAH);
        this.setUtilizaPercentual(true);
    }

}
