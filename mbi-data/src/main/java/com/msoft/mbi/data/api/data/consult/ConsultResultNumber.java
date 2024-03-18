package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class ConsultResultNumber extends ConsultResult {

    private NumberFormat nf;
    
    public ConsultResultNumber() {
        super();
        this.setFormato();
    }
    
    public ConsultResultNumber(Field campo) {
        super(campo);
        this.setFormato();
    }

    public ConsultResultNumber(Field campo, Object valor) {
        super(campo, valor);
        this.setFormato();
    }
    
    public ConsultResultNumber(Field campo, Collection<Object> valores) {
        super(campo, valores);
        this.setFormato();
    }
    
    public ConsultResultNumber(Field campo, ArrayList<Object> valores) {
        super(campo, valores);
        this.setFormato();
    }

    private void setFormato() {
        nf = NumberFormat.getInstance(Locale.GERMANY);;
        nf.setMaximumFractionDigits(this.field.getNumDecimalPositions());
        nf.setMinimumFractionDigits(this.field.getNumDecimalPositions());
    }

    public Object getFormattedValue(int index) {
        Object obj = this.getValor(index);
        if (obj != null) {
            if (obj instanceof Number) {
                obj = nf.format(obj);
                return obj;
            } else if (obj instanceof String && !obj.equals("")) {
                Double d = Double.valueOf(obj.toString());
                return nf.format(d);
            }
        }
        return "-";
    }
}
