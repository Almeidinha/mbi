package com.msoft.mbi.cube.multi.column;

import java.io.Serial;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.OperadoresLogicos;

public class TipoData implements DataType<Date> {

    @Serial
    private static final long serialVersionUID = -5731073170436610239L;

    private Date data = null;
    public static final Date BRANCO = new Date(Integer.MIN_VALUE - 1);

    public Date getValor(ResultSet set, String campo) throws SQLException {
        this.data = set.getDate(campo);
        return this.data;
    }

    public Date format(Date t) {
        if (t == BRANCO || t == null) {
            return null;
        }
        return t;
    }

    @Override
    public OperaTor<Date> getOperator(String operator) {
        if (OperadoresLogicos.MAIOR.equals(operator)) {
            return OperadoresLogicos.operadorMaiorData;
        } else if (OperadoresLogicos.MAIORIGUAL.equals(operator)) {
            return OperadoresLogicos.operadorMaiorIgualData;
        } else if (OperadoresLogicos.IGUAL.equals(operator)) {
            return OperadoresLogicos.operadorIgualDimensao;
        } else if (OperadoresLogicos.MENOR.equals(operator)) {
            return OperadoresLogicos.operadorMenorData;
        } else if (OperadoresLogicos.MENORIGUAL.equals(operator)) {
            return OperadoresLogicos.operadorMenorIgualData;
        } else if (OperadoresLogicos.ENTRE_EXCLUSIVO.equals(operator)) {
            return OperadoresLogicos.operadorEntreExclusivoData;
        } else if (OperadoresLogicos.ENTRE_INCLUSIVO.equals(operator)) {
            return OperadoresLogicos.operadorEntreInclusivoData;
        } else {
            return OperadoresLogicos.operadorDiferenteDimensao;
        }
    }
}
