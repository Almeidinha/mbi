package com.msoft.mbi.cube.multi.column;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.OperadoresLogicos;

import java.io.Serial;

public abstract class TipoNumero<T> implements DataType<T> {

    public static final Number BRANCO = 0;

    @Override
    public OperaTor getOperator(String operator) {
        if (OperadoresLogicos.MAIOR.equals(operator)) {
            return OperadoresLogicos.operadorMaiorNumero;
        } else if (OperadoresLogicos.MAIORIGUAL.equals(operator)) {
            return OperadoresLogicos.operadorMaiorIgualNumero;
        } else if (OperadoresLogicos.IGUAL.equals(operator)) {
            return OperadoresLogicos.operadorIgualNumero;
        } else if (OperadoresLogicos.MENOR.equals(operator)) {
            return OperadoresLogicos.operadorMenorNumero;
        } else if (OperadoresLogicos.MENORIGUAL.equals(operator)) {
            return OperadoresLogicos.operadorMenorIgualNumero;
        } else if (OperadoresLogicos.ENTRE_EXCLUSIVO.equals(operator)) {
            return OperadoresLogicos.operadorEntreNumero;
        } else if (OperadoresLogicos.ENTRE_INCLUSIVO.equals(operator)) {
            return OperadoresLogicos.operadorEntreNumero;
        } else {
            return OperadoresLogicos.operadorDiferenteNumero;
        }
    }

}
