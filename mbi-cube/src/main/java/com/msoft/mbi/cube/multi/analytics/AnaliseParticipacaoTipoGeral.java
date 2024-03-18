package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNula;

import java.io.Serial;

public class AnaliseParticipacaoTipoGeral implements AnaliseParticipacaoTipo {

    private static AnaliseParticipacaoTipoGeral analiseParticipacaoTipoGeral;

    private AnaliseParticipacaoTipoGeral() {
        super();
    }

    public static AnaliseParticipacaoTipoGeral getInstance() {
        if (analiseParticipacaoTipoGeral == null) {
            analiseParticipacaoTipoGeral = new AnaliseParticipacaoTipoGeral();
        }
        return analiseParticipacaoTipoGeral;
    }

    @Override
    public Dimension getDimensaoNivelAcima(Dimension dimensionAtual) {
        return DimensionNula.createDimensaoNula(dimensionAtual.getMetaData().getEixoReferencia(), dimensionAtual.getCube());
    }
}
