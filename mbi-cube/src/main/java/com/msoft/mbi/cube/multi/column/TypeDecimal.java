package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TypeDecimal extends TypeNumber<Double> {

    public static final Double BRANCO = 0.0;

    public Double getValue(ResultSet set, String field) throws SQLException {
        double decimal = set.getDouble(field);
        return ((set.wasNull()) ? 0.0 : decimal);
    }

    public Object format(Double aDouble) {
        if (Objects.equals(aDouble, BRANCO) || aDouble == null) {
            aDouble = 0.0d;
        }

        Integer i = aDouble.intValue();
        if (aDouble > i)
            return aDouble;
        else
            return i;
    }
}
