package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public class AnaliseParticipacaoTipoParcialProxNivel implements AnalysisParticipationType {

    private static AnaliseParticipacaoTipoParcialProxNivel analiseParticipacaoTipoParcialProxNivel;

    private AnaliseParticipacaoTipoParcialProxNivel() {
        super();
    }

    public static AnaliseParticipacaoTipoParcialProxNivel getInstance() {
        if (analiseParticipacaoTipoParcialProxNivel == null) {
            analiseParticipacaoTipoParcialProxNivel = new AnaliseParticipacaoTipoParcialProxNivel();
        }
        return analiseParticipacaoTipoParcialProxNivel;
    }

    @Override
    public Dimension getUpperLevelDimension(Dimension currentDimension) {
        return currentDimension.getParent();
    }
}
