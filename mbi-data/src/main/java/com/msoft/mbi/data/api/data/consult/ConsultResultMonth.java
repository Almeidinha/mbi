package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.ArrayList;
import java.util.Collection;

public class ConsultResultMonth extends ConsultResult {

    public static final String ABREVIADO = "mmm";
    public static final String NAO_ABREVIADO = "mmmm";
    private static final String[] ABREV = { "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez" };
    private static final String[] NAO_ABREV = { "Janeiro", "Fevereiro", "Mar�o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };

    private String formato[];

    public ConsultResultMonth() {
        super();
    }

    public ConsultResultMonth(Field campo) {
        super(campo);
        this.setFormat();
    }

    public ConsultResultMonth(Field campo, Object valor) {
        super(campo, valor);
        this.setFormat();
    }

    public ConsultResultMonth(Field campo, Collection<Object> valores) {
        super(campo, valores);
        this.setFormat();
    }
    
    public ConsultResultMonth(Field campo, ArrayList<Object> valores) {
        super(campo, valores);
        this.setFormat();
    }

    private void setFormat() {
        String mascara = this.field.getDateMask();
        if (mascara != null) {
            if (mascara.equals(ConsultResultMonth.ABREVIADO)) {
                this.formato = ConsultResultMonth.ABREV;
            } else if (mascara.equals(ConsultResultMonth.NAO_ABREVIADO)) {
                this.formato = ConsultResultMonth.NAO_ABREV;
            }
        }
    }

    public Object getFormattedValue(int index) {
        Object object = this.getValor(index);
        if (object != null && this.formato != null) {
            return this.formato[((int) Double.parseDouble(object.toString())) - 1];
        }
        return object;
    }

}
