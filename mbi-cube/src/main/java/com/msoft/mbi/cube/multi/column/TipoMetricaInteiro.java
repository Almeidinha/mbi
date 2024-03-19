package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoMetricaInteiro extends TipoNumero<Double> {


    public static final Integer BRANCO = Integer.MIN_VALUE - 1;

    @Override
    public Double getValue(ResultSet set, String campo) throws SQLException {
        return set.getDouble(campo);
    }

    @Override
    public Integer format(Double t) {
        return t.intValue();
    }

}
