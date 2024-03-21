package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public class AnaliseParticipacaoTipoParcialProxNivelTotalizado implements AnaliseParticipacaoTipo {

    private static AnaliseParticipacaoTipoParcialProxNivelTotalizado analiseParticipacaoTipoParcialProxNivelTotalizado;

    private AnaliseParticipacaoTipoParcialProxNivelTotalizado() {
        super();
    }

    public static AnaliseParticipacaoTipoParcialProxNivelTotalizado getInstance() {
        if (analiseParticipacaoTipoParcialProxNivelTotalizado == null) {
            analiseParticipacaoTipoParcialProxNivelTotalizado = new AnaliseParticipacaoTipoParcialProxNivelTotalizado();
        }
        return analiseParticipacaoTipoParcialProxNivelTotalizado;
    }

    @Override
    public Dimension getDimensaoNivelAcima(Dimension dimensionAtual) {
        return dimensionAtual.getDimensionTotalLevelUp();
    }
}
