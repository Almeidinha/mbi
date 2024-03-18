package com.msoft.mbi.cube.multi.renderers;

import java.text.NumberFormat;
import java.util.Locale;

public class DecimalMaskRender {

    private final int decimalPlaces;

    public DecimalMaskRender(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public DecimalMaskRender() {
        this.decimalPlaces = 0;
    }

    public int getNCasasDecimais() {
        return decimalPlaces;
    }

    public Object apply(Object valor) {
        Double valorD = Double.parseDouble(String.valueOf(valor));
        NumberFormat numberInstance = NumberFormat.getInstance(Locale.GERMANY);
        numberInstance.setMinimumFractionDigits(this.decimalPlaces);
        numberInstance.setMaximumFractionDigits(this.decimalPlaces);
        valor = numberInstance.format(valorD);
        return valor;
    }

}
