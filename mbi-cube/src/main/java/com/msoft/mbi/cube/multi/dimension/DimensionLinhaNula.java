package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;


public class DimensionLinhaNula extends DimensionNula {


    public DimensionLinhaNula(Cubo cubo) {
        super(cubo);
        this.getMetaData().setEixoReferencia(CampoMetaData.LINHA);
    }

    @Override
    public Dimensions getDimensoesAbaixo() {
        return this.cube.getDimensionsLine();
    }

}
