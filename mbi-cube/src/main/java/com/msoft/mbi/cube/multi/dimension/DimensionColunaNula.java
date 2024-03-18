package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;

import java.io.Serial;

public class DimensionColunaNula extends DimensionNula {

    @Serial
    private static final long serialVersionUID = -5286141683114265927L;

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
