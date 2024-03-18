package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;

import java.io.Serial;

public class DimensionLinhaNula extends DimensionNula {

    @Serial
    private static final long serialVersionUID = -3040277667234384977L;

    public DimensionLinhaNula(Cubo cubo) {
        super(cubo);
        this.getMetaData().setEixoReferencia(CampoMetaData.LINHA);
    }

    @Override
    public Dimensions getDimensoesAbaixo() {
        return this.cube.getDimensionsLine();
    }

}
