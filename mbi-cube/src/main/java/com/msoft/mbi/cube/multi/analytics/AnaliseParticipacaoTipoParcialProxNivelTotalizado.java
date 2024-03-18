package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

import java.io.Serial;

public class AnaliseParticipacaoTipoParcialProxNivelTotalizado implements AnaliseParticipacaoTipo {

    @Serial
    private static final long serialVersionUID = 4925420157797689212L;

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
        return dimensionAtual.getDimensionTotalizedLevelUp();
    }
}
