package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;


public class DimensionColunaNula extends DimensionNula {

    public DimensionColunaNula(Cubo cubo) {
        super(cubo);
        this.getMetaData().setEixoReferencia(CampoMetaData.COLUNA);
    }

    @Override
    public Dimensions getDimensoesAbaixo() {
        return this.cube.getDimensionsColumn();
    }

    @Override
    public int getTotalSize() {
        return this.cube.getDimensoesUltimoNivelColuna().size();
    }

}
