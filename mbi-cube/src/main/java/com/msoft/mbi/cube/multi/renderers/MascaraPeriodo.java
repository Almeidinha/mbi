package com.msoft.mbi.cube.multi.renderers;

import java.io.Serial;

public class MascaraPeriodo implements MascaraRenderer, MascaraEisDimensaoDat {

    @Serial
    private static final long serialVersionUID = -4737368669194947013L;
    public static final String MASCARA_NUMERO = "n";
    public static final String MASCARA_PERIODO_ABREVIADO = "nnn";
    public static final String MASCARA_PERIODO_NAO_ABREVIADO = "nnnn";
    private static final String[] PERIODO_ABREVIADO = {"Pri", "Seg", "Ter", "Qua", "Qui", "Sex"};
    private static final String[] PERIODO_NAO_ABREVIADO = {"Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto", "Sexto"};
    private String mascaraComplemento = null;
    private String[] mascaraNumero;
    private String mascara;

    public MascaraPeriodo() {
    }

    public MascaraPeriodo(String mascara) {
        this.mascara = mascara;
        this.resolveMascara();

    }

    @Override
    public void resolveMascara() {
        String mascaraNumero = null;
        if (mascara.contains("'")) {
            mascaraNumero = mascara.substring(0, mascara.indexOf("'")).trim();
            this.mascaraComplemento = mascara.substring(mascara.indexOf("'") + 1, mascara.lastIndexOf("'"));
        } else {
            mascaraNumero = mascara.trim();
        }
        if (MascaraPeriodo.MASCARA_PERIODO_ABREVIADO.equals(mascaraNumero)) {
            this.mascaraNumero = MascaraPeriodo.PERIODO_ABREVIADO;
        } else if (MascaraPeriodo.MASCARA_PERIODO_NAO_ABREVIADO.equals(mascaraNumero)) {
            this.mascaraNumero = MascaraPeriodo.PERIODO_NAO_ABREVIADO;
        }

    }

    @Override
    public Object aplica(Object valor) {
        if (this.mascaraNumero == null) {
            if (this.mascaraComplemento == null) {
                return valor;
            } else {
                return (((int) Double.parseDouble(valor.toString())) + this.mascaraComplemento);
            }
        } else {
            if (this.mascaraComplemento == null) {
                return this.mascaraNumero[((int) Double.parseDouble(valor.toString()) - 1)];
            } else {
                return (this.mascaraNumero[((int) Double.parseDouble(valor.toString()) - 1)] + this.mascaraComplemento);
            }
        }
    }

    public static boolean validaMascara(String mascara) {
        if (mascara != null) {
            return mascara.contains("n");
        }
        return false;

    }

}
