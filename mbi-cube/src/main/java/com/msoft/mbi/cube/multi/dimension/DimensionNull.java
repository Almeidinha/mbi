package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;

public abstract class DimensionNull extends Dimension {

    protected DimensionNull(Cube cube) {
        super(new DimensionMetaData(null, null, null));
        this.cube = cube;
        this.keyValue = EMPTY;
    }

    public static DimensionNull createDimensionNull(int referenceAxis, Cube cube) {
        if (referenceAxis == MetaDataField.LINE) {
            return new DimensionLineNull(cube);
        } else {
            return new DimensionNullColumn(cube);
        }
    }

    public void process(ResultSet set) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setKeyValue() {
        this.keyValue = EMPTY;
    }

}
