package com.msoft.mbi.cube.multi.colorAlertCondition;

import java.io.Serial;

public class TipoComparacaoOutroCampoValor implements TipoComparacaoOutroCampo {

    @Serial
    private static final long serialVersionUID = 4764168851322425336L;

    private static TipoComparacaoOutroCampoValor tipoComparacaoOutroCampoValor;

    private TipoComparacaoOutroCampoValor() {
        super();
    }

    public static TipoComparacaoOutroCampoValor getInstance() {
        if (tipoComparacaoOutroCampoValor == null) {
            tipoComparacaoOutroCampoValor = new TipoComparacaoOutroCampoValor();
        }
        return tipoComparacaoOutroCampoValor;
    }

    @Override
    public Double getValorComparar(Double valorOutroCampo, Double valorReferencia) {
        return valorOutroCampo + valorReferencia;
    }
}
