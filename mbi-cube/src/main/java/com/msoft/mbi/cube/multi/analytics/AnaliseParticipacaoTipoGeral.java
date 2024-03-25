package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNull;

public class AnaliseParticipacaoTipoGeral implements AnalysisParticipationType {

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
    public Dimension getUpperLevelDimension(Dimension currentDimension) {
        return DimensionNull.createDimensionNull(currentDimension.getMetaData().getReferenceAxis(), currentDimension.getCube());
    }
}
