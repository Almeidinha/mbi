package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public interface AnaliseEvolucaoTipo {

  Dimension getDimensaoAnterior(Dimension dimensionAtual);

}
