package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class ConsultResultDate extends ConsultResult {
    private SimpleDateFormat df;

    public ConsultResultDate() {
        super();
        this.setFormat();
    }

    public ConsultResultDate(Field campo, Object valor) {
        super(campo, valor);
        this.setFormat();
    }

    public ConsultResultDate(Field campo, Collection<Object> valores) {
        super(campo, valores);
        this.setFormat();
    }

    public ConsultResultDate(Field campo) {
        super(campo);
        this.setFormat();
    }

    public ConsultResultDate(Field campo, ArrayList<Object> valores) {
        super(campo, valores);
        this.setFormat();
    }

    private void setFormat() {
        Locale l = new Locale("pt", "BR");
        df = new SimpleDateFormat("dd/MM/yyyy", l);
        String mascara = this.field.getDateMask();
        if (mascara != null && !mascara.isEmpty()) {
            df.applyPattern(mascara);
        }
    }

    public Object getFormattedValue(int index) {
        Object obj = this.getValor(index);
        if (obj != null) {
            if (obj instanceof Date) {
                return df.format(obj);
            }
            return obj;
        }
        return "-";
    }
    
}
