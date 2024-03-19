package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoDecimal extends TipoNumero<Double> {

    private Double decimal = null;
    public static final Double BRANCO = Double.valueOf(Integer.MIN_VALUE - 1);

    public Double getValue(ResultSet set, String campo) throws SQLException {
        this.decimal = set.getDouble(campo);
        return ((set.wasNull()) ? Double.valueOf(0) : this.decimal);
    }

    public Object format(Double t) {
        if (t == BRANCO || t == null) {
            t = 0.0d;
        }

        Integer i = t.intValue();
        if (t > i)
            return t;
        else
            return i;
    }
}
