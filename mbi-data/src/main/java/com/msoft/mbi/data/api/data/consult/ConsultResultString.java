package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.ArrayList;
import java.util.Collection;

public class ConsultResultString extends ConsultResult {

    public ConsultResultString() {
        super();
    }

    public ConsultResultString(Field campo) {
        super(campo);
    }

    public ConsultResultString(Field campo, Object valor) {
        super(campo, valor);
    }

    public ConsultResultString(Field campo, Collection<Object> valores) {
        super(campo, valores);
    }

    public ConsultResultString(Field campo, ArrayList<Object> valores) {
        super(campo, valores);
    }

    public Object getFormattedValue(int index) {
        return this.getValor(index);
    }
}
