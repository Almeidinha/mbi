package com.msoft.mbi.cube.multi.column;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;

public class TipoHora implements DataType<Time> {

    @Serial
    private static final long serialVersionUID = -2677223042679361900L;

    private Time hora = null;
    public static final Time BRANCO = new Time(Integer.MIN_VALUE - 1);

    public Time getValor(ResultSet set, String campo) throws SQLException {
        this.hora = set.getTime(campo);
        return this.hora;
    }

    public Time format(Time t) {
        if (t == BRANCO || t == null) {
            return null;
        }
        return t;
    }

    @Override
    public OperaTor getOperator(String operator) {
        return null;
    }

}
