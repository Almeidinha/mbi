package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public class AnaliseEvolucaoTipoFixa implements EvolutionAnalysisType {

    private static AnaliseEvolucaoTipoFixa analiseEvolucaoTipoFixa;

    private AnaliseEvolucaoTipoFixa() {
        super();
    }

    public static AnaliseEvolucaoTipoFixa getInstance() {
        if (analiseEvolucaoTipoFixa == null) {
            analiseEvolucaoTipoFixa = new AnaliseEvolucaoTipoFixa();
        }
        return analiseEvolucaoTipoFixa;
    }

    @Override
    public Dimension getPreviousDimension(Dimension currentDimension) {
        return currentDimension.getSameLevelFirstDimensionColumn();
    }

}
