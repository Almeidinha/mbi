package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;


public class DimensionLinhaNull extends DimensionNull {


    public DimensionLinhaNull(Cube cube) {
        super(cube);
        this.getMetaData().setEixoReferencia(MetaDataField.LINE);
    }

    @Override
    public Dimensions getDimensionsBelow() {
        return this.cube.getDimensionsLine();
    }

}
