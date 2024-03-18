package com.msoft.mbi.cube.multi.column;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoTextoRoot extends TipoTexto {

    @Serial
    private static final long serialVersionUID = -624288302954193444L;

    @Override
    public String getValor(ResultSet set, String campo) throws SQLException {
        return "ROOT";
    }
}
