package com.msoft.mbi.cube.multi.renderers;

import java.text.NumberFormat;
import java.util.Locale;

public class DecimalMaskRenderer {

    private final int decimalPlaces;

    public DecimalMaskRenderer(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public DecimalMaskRenderer() {
        this.decimalPlaces = 0;
    }

    public int getDecimalPlacesCount() {
        return decimalPlaces;
    }

    public Object apply(Object value) {
        Double valorD = Double.parseDouble(String.valueOf(value));
        NumberFormat numberInstance = NumberFormat.getInstance(Locale.GERMANY);
        numberInstance.setMinimumFractionDigits(this.decimalPlaces);
        numberInstance.setMaximumFractionDigits(this.decimalPlaces);
        value = numberInstance.format(valorD);
        return value;
    }

}
