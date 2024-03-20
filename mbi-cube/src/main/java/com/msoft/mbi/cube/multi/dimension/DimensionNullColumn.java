package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;


public class DimensionNullColumn extends DimensionNull {

    public DimensionNullColumn(Cube cube) {
        super(cube);
        this.getMetaData().setEixoReferencia(MetaDataField.COLUMN);
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
