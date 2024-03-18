package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

import java.io.Serial;

public class AnaliseParticipacaoTipoParcialProxNivel implements AnaliseParticipacaoTipo {

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
    public Dimension getDimensaoNivelAcima(Dimension dimensionAtual) {
        return dimensionAtual.getParent();
    }
}
