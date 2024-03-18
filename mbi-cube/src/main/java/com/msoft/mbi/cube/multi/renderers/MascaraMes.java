package com.msoft.mbi.cube.multi.renderers;


public class MascaraMes implements MascaraRenderer, MascaraEisDimensaoDat {

    public static final String ABREVIADO = "MMM";
    public static final String NAO_ABREVIADO = "MMMM";
    private static final String[] ABREV = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
    private static final String[] NAO_ABREV = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private String[] formato;
    private String mascara = "";

    public MascaraMes(String mascara) {
        this.mascara = mascara;
        this.resolveMascara();
    }

    public void resolveMascara() {
        if (mascara != null) {
            if (mascara.equals(MascaraMes.ABREVIADO)) {
                this.formato = MascaraMes.ABREV;
            } else if (mascara.equals(MascaraMes.NAO_ABREVIADO)) {
                this.formato = MascaraMes.NAO_ABREV;
            }
        }
    }

    @Override
    public Object aplica(Object valor) {
        if (valor != null && this.formato != null) {
            if (valor.toString().contains(";")) {
                return valor;
            }
            return this.formato[((int) Double.parseDouble(valor.toString())) - 1];
        }
        return valor;
    }

}
