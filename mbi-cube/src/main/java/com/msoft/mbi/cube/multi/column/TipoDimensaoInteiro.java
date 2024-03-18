package com.msoft.mbi.cube.multi.column;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoDimensaoInteiro extends TipoNumero<Integer> {

    @Serial
    private static final long serialVersionUID = -5514610762511394703L;
    private Integer inteiro = null;
    public static final Integer BRANCO = Integer.MIN_VALUE - 1;

    public Integer getValor(ResultSet set, String campo) throws SQLException {
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
