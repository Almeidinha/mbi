package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.ArrayList;
import java.util.Collection;

public class ConsultResultWeek extends ConsultResult {
    
    public static final String ABREVIADO = "sss";
    public static final String NAO_ABREVIADO = "ssss";
    private static final String[] ABREV = { "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab" };
    private static final String[] NAO_ABREV = { "Domingo", "Segunda", "Ter�a", "Quarta", "Quinta", "Sexta", "S�bado" };
    private String[] mascara;
    
    public ConsultResultWeek() {
        super();
        this.setFormat();
    }
    
    public ConsultResultWeek(Field campo) {
        super(campo);
        this.setFormat();
    }
    
    public ConsultResultWeek(Field campo, Object valor) {
        super(campo, valor);
        this.setFormat();
    }
    
    public ConsultResultWeek(Field campo, Collection<Object> valores) {
        super(campo, valores);
        this.setFormat();
    }

    public ConsultResultWeek(Field campo, ArrayList<Object> valores) {
        super(campo, valores);
        this.setFormat();
    }
    
    private void setFormat() {
        String mascara = this.field.getDateMask();
        if (mascara != null) {
            if (mascara.equals(ConsultResultWeek.ABREVIADO)) {
                this.mascara = ConsultResultWeek.ABREV;
            } else if (mascara.equals(ConsultResultWeek.NAO_ABREVIADO)) {
                this.mascara = ConsultResultWeek.NAO_ABREV;
            }
        }
    }

    public Object getFormattedValue(int index) {
        Object object = this.getValor(index);
        if (this.mascara != null){
            return this.mascara[((int) Double.parseDouble(object.toString())) - 2];
        }
        return object;
    }
}
