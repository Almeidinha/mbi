package com.msoft.mbi.cube.multi.column;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoMetricaInteiro extends TipoNumero<Double> {

    @Serial
    private static final long serialVersionUID = -5514610762511394703L;

    public static final Integer BRANCO = Integer.MIN_VALUE - 1;

    @Override
    public Double getValor(ResultSet set, String campo) throws SQLException {
        return set.getDouble(campo);
    }

    @Override
    public Integer format(Double t) {
        return t.intValue();
    }

}
