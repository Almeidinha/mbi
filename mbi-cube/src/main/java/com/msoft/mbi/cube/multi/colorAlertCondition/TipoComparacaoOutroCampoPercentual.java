package com.msoft.mbi.cube.multi.colorAlertCondition;


public class TipoComparacaoOutroCampoPercentual implements TipoComparacaoOutroCampo {

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
