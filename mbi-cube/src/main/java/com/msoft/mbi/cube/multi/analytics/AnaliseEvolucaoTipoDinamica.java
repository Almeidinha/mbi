package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

import java.io.Serial;

public class AnaliseEvolucaoTipoDinamica implements AnaliseEvolucaoTipo {

    @Serial
    private static final long serialVersionUID = 1908972561906857406L;

    private static AnaliseEvolucaoTipoDinamica analiseEvolucaoTipoDinamica;

    private AnaliseEvolucaoTipoDinamica() {
        super();
    }

    public static AnaliseEvolucaoTipoDinamica getInstance() {
        if (analiseEvolucaoTipoDinamica == null) {
            analiseEvolucaoTipoDinamica = new AnaliseEvolucaoTipoDinamica();
        }
        return analiseEvolucaoTipoDinamica;
    }

    @Override
    public Dimension getDimensaoAnterior(Dimension dimensionAtual) {
        return dimensionAtual.getPreviousDimension(dimensionAtual.getCube());
    }

}
