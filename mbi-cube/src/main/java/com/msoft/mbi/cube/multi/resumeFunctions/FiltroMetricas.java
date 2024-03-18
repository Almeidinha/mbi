package com.msoft.mbi.cube.multi.resumeFunctions;

import java.util.List;
import java.util.Set;

import com.msoft.mbi.cube.exception.CuboMathParserException;
import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

public class FiltroMetricas {

    protected Calculo expressaoFiltro;
    private MetricaValorUtilizar metricaValorUtilizar;

    public FiltroMetricas(String expressao, MetricaValorUtilizar metricaValorUtilizar) {
        this.metricaValorUtilizar = metricaValorUtilizar;
        expressao = "IF((" + expressao + "), 1, 0)";
        this.expressaoFiltro = new Calculo(expressao);
    }

    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo) {
        return this.metricaValorUtilizar.getDimensoesColunaUtilizar(cubo);
    }

    public boolean testaCondicao(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica) {
        boolean retorno;
        Set<String> variables = this.expressaoFiltro.getVariables().keySet();
        try {
            for (String variable : variables) {
                String tituloMetrica = this.expressaoFiltro.getVariables().get(variable);
                Double valorMetrica = this.metricaValorUtilizar.getValor(mapaMetricas, linhaMetrica, tituloMetrica);
                this.expressaoFiltro.setValorVariable(variable, valorMetrica);
            }
            retorno = this.expressaoFiltro.calculaValor() == 1;
        } catch (Exception e) {
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível testar a condição do filtro métrica.", e);
            throw parserException;
        }
        return retorno;
    }

}
