package com.msoft.mbi.cube.multi.renderers;

import java.io.Serial;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class MascaraCasasDecimaisRenderer implements Serializable {

    @Serial
    private static final long serialVersionUID = 4004553153554390236L;

    private int nCasasDecimais;

    public MascaraCasasDecimaisRenderer(int nCasasDecimais) {
        this.nCasasDecimais = nCasasDecimais;
    }

    public MascaraCasasDecimaisRenderer() {
        this.nCasasDecimais = 0;
    }

    public int getNCasasDecimais() {
        return nCasasDecimais;
    }

    public Object aplica(Object valor) {
        Double valorD = Double.parseDouble(String.valueOf(valor));
        NumberFormat numberInstance = NumberFormat.getInstance(Locale.GERMANY);
        numberInstance.setMinimumFractionDigits(this.nCasasDecimais);
        numberInstance.setMaximumFractionDigits(this.nCasasDecimais);
        valor = numberInstance.format(valorD);
        return valor;
    }

}
