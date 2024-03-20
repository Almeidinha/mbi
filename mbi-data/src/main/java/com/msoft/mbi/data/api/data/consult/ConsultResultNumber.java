package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.exception.BIFilterException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.util.BIUtil;

import java.util.ArrayList;
import java.util.Collection;

public class ConsultResultNumber extends ConsultResult {

    
    public ConsultResultNumber() {
        super();
    }
    
    public ConsultResultNumber(Field campo) {
        super(campo);
    }

    public ConsultResultNumber(Field campo, Object valor) {
        super(campo, valor);
    }
    
    public ConsultResultNumber(Field campo, Collection<Object> values) {
        super(campo, values);
    }
    
    public ConsultResultNumber(Field campo, ArrayList<Object> values) {
        super(campo, values);
    }

    public Object getFormattedValue(int index) throws BIFilterException {
        Object obj = this.getValor(index);
        if (obj != null) {
            if (obj instanceof Number) {
                return BIUtil.formatDoubleToTextObject(String.valueOf(obj), this.field.getNumDecimalPositions());
            } else if (obj instanceof String && !obj.equals("")) {
                return BIUtil.formatDoubleToTextObject(String.valueOf(obj), this.field.getNumDecimalPositions());
            }
        }
        return "-";
    }
}
