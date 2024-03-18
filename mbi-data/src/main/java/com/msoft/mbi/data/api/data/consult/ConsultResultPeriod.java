package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.ArrayList;
import java.util.Collection;

public class ConsultResultPeriod extends ConsultResult {

    public static final String MASCARA_NUMERO = "n";
    public static final String MASCARA_PERIODO_ABREVIADO = "nnn";
    public static final String MASCARA_PERIODO_NAO_ABREVIADO = "nnnn";
    private static final String[] PERIODO_ABREVIADO = { "Pri", "Seg", "Ter", "Qua", "Qui", "Sex" };
    private static final String[] PERIODO_NAO_ABREVIADO = { "Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto", "Sexto" };
    private String mascaraComplemento = null;
    private String[] mascaraNumero;
    
    public ConsultResultPeriod() {
        super();
        this.setMascara(field.getDateMask());
    }
    
    public ConsultResultPeriod(Field campo) {
        super(campo);
        this.setMascara(campo.getDateMask());
    }
    
    public ConsultResultPeriod(Field campo, Object valor) {
        super(campo, valor);
        this.setMascara(campo.getDateMask());
    }
    
    public ConsultResultPeriod(Field campo, Collection<Object> valores) {
        super(campo, valores);
        this.setMascara(campo.getDateMask());
    }
    
    public ConsultResultPeriod(Field campo, ArrayList<Object> valores) {
        super(campo, valores);
    }

    public Object getFormattedValue(int index) {
        if (this.mascaraNumero == null) {
            if (this.mascaraComplemento == null) {
                return this.getValor(index);
            } else {
                return (((int) Double.parseDouble(this.getValor(index).toString())) + this.mascaraComplemento);
            }
        } else {
            if (this.mascaraComplemento == null) {
                return this.mascaraNumero[((int) Double.parseDouble(this.getValor(index).toString()) - 1)];
            } else {
                return (this.mascaraNumero[((int) Double.parseDouble(this.getValor(index).toString()) - 1)] + this.mascaraComplemento);
            }
        }
    }

    private void setMascara(String mascara) {
        String mascaraNumero;
        if (mascara.contains("'")) {
            mascaraNumero = mascara.substring(0, mascara.indexOf("'")).trim();
            this.mascaraComplemento = mascara.substring(mascara.indexOf("'") + 1, mascara.lastIndexOf("'"));
        } else {
            mascaraNumero = mascara.trim();
        }
        if (ConsultResultPeriod.MASCARA_PERIODO_ABREVIADO.equals(mascaraNumero)) {
            this.mascaraNumero = ConsultResultPeriod.PERIODO_ABREVIADO;
        } else if (ConsultResultPeriod.MASCARA_PERIODO_NAO_ABREVIADO.equals(mascaraNumero)) {
            this.mascaraNumero = ConsultResultPeriod.PERIODO_NAO_ABREVIADO;
        }
    }

    public static boolean validaMascara(String mascara) {
        if (mascara != null) {
            return mascara.contains("n");
        }
        return false;

    }
}
