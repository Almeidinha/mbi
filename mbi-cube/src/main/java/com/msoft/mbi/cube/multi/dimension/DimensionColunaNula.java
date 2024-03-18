package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;


public class DimensionColunaNula extends DimensionNula {

    public DimensionColunaNula(Cube cube) {
        super(cube);
        this.getMetaData().setEixoReferencia(CampoMetaData.COLUNA);
    }

    @Override
    public Dimensions getDimensionsBelow() {
        return this.cube.getDimensionsColumn();
    }

    @Override
    public int getTotalSize() {
        return this.cube.getDimensionsLastLevelColumns().size();
    }

}
