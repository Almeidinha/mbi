package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public class AnaliseEvolucaoTipoDinamica implements EvolutionAnalysisType {

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
    public Dimension getPreviousDimension(Dimension currentDimension) {
        return currentDimension.getPreviousDimension(currentDimension.getCube());
    }

}
