package com.msoft.mbi.cube.multi.resumeFunctions;

import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarTotal;

public class FiltroMetricasValorAcumulado extends FiltroMetricas {

    public FiltroMetricasValorAcumulado(String expressao) {
        super(expressao, MetricaValorUtilizarTotal.getInstance());
        for (String variable : this.expressaoFiltro.getVariables().keySet()) {
            String tituloCampo = this.expressaoFiltro.getVariables().get(variable);
            tituloCampo = tituloCampo.substring(5, tituloCampo.length() - 1);
            this.expressaoFiltro.putVariable(variable, tituloCampo);
        }
    }
}
