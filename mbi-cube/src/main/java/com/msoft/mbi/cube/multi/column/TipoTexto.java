package com.msoft.mbi.cube.multi.column;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.OperadoresLogicos;

public class TipoTexto implements DataType<String> {

    @Serial
    private static final long serialVersionUID = -679817482559706559L;

    public static final String BRANCO = "";
    private String texto = null;

    public String getValor(ResultSet set, String campo) throws SQLException {
        this.texto = set.getString(campo);
        return ((set.wasNull()) ? BRANCO : this.texto.trim());
    }

    public String format(String t) {
        return t;
    }

    @Override
    public OperaTor<String> getOperator(String operator) {
        if (OperadoresLogicos.IGUAL.equals(operator)) {
            return OperadoresLogicos.operadorIgualDimensao;
        } else if (OperadoresLogicos.INICIACOM.equals(operator)) {
            return OperadoresLogicos.operadorIniciaComTexto;
        } else if (OperadoresLogicos.CONTEM.equals(operator)) {
            return OperadoresLogicos.operadorContemTexto;
        } else if (OperadoresLogicos.NAOCONTEM.equals(operator)) {
            return OperadoresLogicos.operadorNaoContemTexto;
        } else {
            return OperadoresLogicos.operadorDiferenteDimensao;
        }
    }

}
