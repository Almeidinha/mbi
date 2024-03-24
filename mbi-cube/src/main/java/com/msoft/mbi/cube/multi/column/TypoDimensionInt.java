package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TypoDimensionInt extends TypeNumber<Integer> {

    public static final Integer BRANCO = - 1;

    public Integer getValue(ResultSet set, String campo) throws SQLException {
        int intValue = set.getInt(campo);
        return ((set.wasNull() ? BRANCO : intValue));
    }

    public Integer format(Integer intValue) {
        if (Objects.equals(intValue, BRANCO)) {
            return null;
        }
        return intValue;
    }

}
