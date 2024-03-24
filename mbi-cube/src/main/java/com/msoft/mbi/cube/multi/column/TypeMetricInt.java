package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeMetricInt extends TypeNumber<Double> {


    public static final Integer BRANCO = - 1;

    @Override
    public Double getValue(ResultSet set, String campo) throws SQLException {
        return set.getDouble(campo);
    }

    @Override
    public Integer format(Double aDouble) {
        return aDouble.intValue();
    }

}
