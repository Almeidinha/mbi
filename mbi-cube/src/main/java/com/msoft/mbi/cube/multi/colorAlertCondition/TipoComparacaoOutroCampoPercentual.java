package com.msoft.mbi.cube.multi.colorAlertCondition;

import java.io.Serial;

public class TipoComparacaoOutroCampoPercentual implements TipoComparacaoOutroCampo {

    @Serial
    private static final long serialVersionUID = 5640813997141053471L;

    private static TipoComparacaoOutroCampoPercentual tipoComparacaoOutroCampoPercentual;

    private TipoComparacaoOutroCampoPercentual() {
        super();
    }

    public static TipoComparacaoOutroCampoPercentual getInstance() {
        if (tipoComparacaoOutroCampoPercentual == null) {
            tipoComparacaoOutroCampoPercentual = new TipoComparacaoOutroCampoPercentual();
        }
        return tipoComparacaoOutroCampoPercentual;
    }

    @Override
    public Double getValorComparar(Double valorOutroCampo, Double valorReferencia) {
        return (valorOutroCampo * valorReferencia) / 100;
    }

}
