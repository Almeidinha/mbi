package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoDimensaoInteiro extends TipoNumero<Integer> {

    private Integer inteiro = null;
    public static final Integer BRANCO = Integer.MIN_VALUE - 1;

    public Integer getValue(ResultSet set, String campo) throws SQLException {
        this.inteiro = set.getInt(campo);
        return ((set.wasNull() ? BRANCO : this.inteiro));
    }

    public Integer format(Integer t) {
        if (t == BRANCO) {
            return null;
        }
        return t;
    }

}
