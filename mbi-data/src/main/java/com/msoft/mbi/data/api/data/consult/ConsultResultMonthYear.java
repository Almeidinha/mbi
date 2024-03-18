package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.ArrayList;
import java.util.Collection;

public class ConsultResultMonthYear extends ConsultResult {

    public static final String MES_ABREVIADO = "mmm";
    public static final String MES_NAO_ABREVIADO = "mmmm";
    public static final String ANO_ABREVIADO = "yy";
    public static final String ANO_NAO_ABREVIADO = "yyyy";
    private static final String[] MES_ABREV = { "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez" };
    private static final String[] MES_NAO_ABREV = { "Janeiro", "Fevereiro", "Mar�o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
    private String[] mascaraMes;
    private String mascaraAno;
    private String separador = "";
    private int modelo;

    public ConsultResultMonthYear() {
        super();
        this.setMascara();
    }
    
    public ConsultResultMonthYear(Field campo) {
        super(campo);
        this.setMascara();
    }

    public ConsultResultMonthYear(Field campo, Object valor) {
        super(campo, valor);
        this.setMascara();
    }

    public ConsultResultMonthYear(Field campo, Collection<Object> valores) {
        super(campo, valores);
        this.setMascara();
    }

    public ConsultResultMonthYear(Field campo, ArrayList<Object> valores) {
        super(campo, valores);
        this.setMascara();
    }

    public Object getFormattedValue(int index) {
        String valor = String.valueOf(this.getValor(index));
        valor = valor.trim();
        if (!valor.equalsIgnoreCase("")) {
            String mes = valor.substring(5, 7);
            String ano = valor.substring(0, 4);
            return this.concatena(mes, ano);
        }
        return "";
    }

    private void setMascara() {
        String mascara = this.field.getDateMask();
        String mascaraMes = null;
        if (!mascara.isEmpty()) {
            String aux = mascara.substring(0, mascara.indexOf("'"));
   
            if (aux.equals("mmm") || aux.equals("mmmm")) {
                mascaraMes = aux;
                this.separador = mascara.substring(mascara.indexOf("'") + 1, mascara.lastIndexOf("'"));
                this.mascaraAno = mascara.substring(mascara.lastIndexOf("'") + 1);
                this.modelo = 0;
            } else if (aux.equals("yy") || aux.equals("yyyy")) {
                this.mascaraAno = aux;
                this.separador = mascara.substring(mascara.indexOf("'") + 1, mascara.lastIndexOf("'"));
                mascaraMes = mascara.substring(mascara.lastIndexOf("'") + 1);
                this.modelo = 1;
            }
            if (ConsultResultMonthYear.MES_ABREVIADO.equals(mascaraMes)) {
                this.mascaraMes = ConsultResultMonthYear.MES_ABREV;
            } else {
                this.mascaraMes = ConsultResultMonthYear.MES_NAO_ABREV;
            }
        } else {
            this.mascaraAno = null;
            this.mascaraMes = null;
            this.separador = "";
        }
    }

    private String concatena(String mes, String ano) {
      
        if (this.mascaraMes != null) {
            mes = this.mascaraMes[((int) Double.parseDouble(mes)) - 1];
        }
        if (this.mascaraAno != null && this.mascaraAno.equals(ConsultResultMonthYear.ANO_ABREVIADO)) 
            ano = ano.substring(2);
        if (this.modelo == 0) 
            return (mes + this.separador + ano);
        else
            return (ano + this.separador + mes);
    }

    public static boolean validaMascara(String mascara) {
        if (mascara != null) {
            return (mascara.contains("mmm")) && (mascara.contains("yy"));
        }
        return false;

    }
}
