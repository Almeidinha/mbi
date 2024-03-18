package com.msoft.mbi.cube.multi.dimension;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;

public abstract class DimensionNula extends Dimension {

    @Serial
    private static final long serialVersionUID = -1646061639216450074L;

    protected DimensionNula(Cubo cubo) {
        super(new DimensaoMetaData(null, null, null));
        this.cube = cubo;
        this.keyValue = BRANCO;
    }

    public static DimensionNula createDimensaoNula(int eixoReferencia, Cubo cubo) {
        if (eixoReferencia == CampoMetaData.LINHA) {
            return new DimensionLinhaNula(cubo);
        } else {
            return new DimensionColunaNula(cubo);
        }
    }

    public void processar(ResultSet set) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setKeyValue() {
        this.keyValue = BRANCO;
    }

}
