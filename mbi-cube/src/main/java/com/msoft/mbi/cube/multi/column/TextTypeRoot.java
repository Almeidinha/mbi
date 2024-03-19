package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TextTypeRoot extends TextType {


    @Override
    public String getValue(ResultSet set, String campo) throws SQLException {
        return "ROOT";
    }
}
