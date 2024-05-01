package com.msoft.mbi.cube.multi.coloralertcondition;

public class TipoComparacaoOutroCampoValor implements TipoComparacaoOutroCampo {

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
