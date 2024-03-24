package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;


public class DimensionLineNull extends DimensionNull {


    public DimensionLineNull(Cube cube) {
        super(cube);
        this.getMetaData().setReferenceAxis(MetaDataField.LINE);
    }

    @Override
    public Dimensions getDimensionsBelow() {
        return this.cube.getDimensionsLine();
    }

}
