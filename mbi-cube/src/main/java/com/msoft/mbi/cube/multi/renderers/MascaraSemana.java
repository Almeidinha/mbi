package com.msoft.mbi.cube.multi.renderers;

public class MascaraSemana implements MascaraRenderer, MascaraEisDimensaoDat {


    public static final String ABREVIADO = "sss";
    public static final String NAO_ABREVIADO = "ssss";
    private static final String[] ABREV = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};
    private static final String[] NAO_ABREV = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};
    private String[] mascaras;
    private String mascara;

    public MascaraSemana(String mascara) {
        this.mascara = mascara;
        this.resolveMascara();
    }

    @Override
    public void resolveMascara() {
        if (mascara != null) {
            if (mascara.equals(MascaraSemana.ABREVIADO)) {
                this.mascaras = MascaraSemana.ABREV;
            } else if (mascara.equals(MascaraSemana.NAO_ABREVIADO)) {
                this.mascaras = MascaraSemana.NAO_ABREV;
            }
        }
    }

    @Override
    public Object aplica(Object valor) {
        if (this.mascaras != null) {
            return this.mascaras[((int) Double.parseDouble(valor.toString())) - 1];
        }
        return valor;
    }

}
