package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;

public abstract class DimensionNula extends Dimension {

    protected DimensionNula(Cube cube) {
        super(new DimensaoMetaData(null, null, null));
        this.cube = cube;
        this.keyValue = BRANCO;
    }

    public static DimensionNula createDimensaoNula(int eixoReferencia, Cube cube) {
        if (eixoReferencia == CampoMetaData.LINHA) {
            return new DimensionLinhaNula(cube);
        } else {
            return new DimensionColunaNula(cube);
        }
    }

    public void process(ResultSet set) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setKeyValue() {
        this.keyValue = BRANCO;
    }

}
