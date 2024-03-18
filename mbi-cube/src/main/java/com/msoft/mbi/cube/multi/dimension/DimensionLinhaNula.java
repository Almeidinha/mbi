package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;


public class DimensionLinhaNula extends DimensionNula {


    public DimensionLinhaNula(Cube cube) {
        super(cube);
        this.getMetaData().setEixoReferencia(CampoMetaData.LINHA);
    }

    @Override
    public Dimensions getDimensionsBelow() {
        return this.cube.getDimensionsLine();
    }

}
