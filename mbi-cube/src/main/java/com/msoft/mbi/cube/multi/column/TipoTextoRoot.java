package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoTextoRoot extends TipoTexto {


    @Override
    public String getValor(ResultSet set, String campo) throws SQLException {
        return "ROOT";
    }
}
