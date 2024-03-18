package com.msoft.mbi.cube.multi.dimension;

import com.msoft.mbi.cube.multi.column.TipoTextoRoot;
import java.io.Serial;

public class DimensaoAuxiliarMetaData extends DimensaoMetaData {

    @Serial
    private static final long serialVersionUID = 6854043120623682238L;

    public DimensaoAuxiliarMetaData() {
        super("Dimensao Auxiliar", "linha", new TipoTextoRoot());
    }

}
