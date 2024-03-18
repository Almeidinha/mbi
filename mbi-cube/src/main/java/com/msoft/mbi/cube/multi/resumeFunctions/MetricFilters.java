package com.msoft.mbi.cube.multi.resumeFunctions;

import java.util.List;
import java.util.Set;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

public class MetricFilters {

    protected Calculo filterExpression;
    private final MetricaValorUtilizar metricValueUse;

    public MetricFilters(String expressao, MetricaValorUtilizar metricValueUse) {
        this.metricValueUse = metricValueUse;
        expressao = "IF((" + expressao + "), 1, 0)";
        this.filterExpression = new Calculo(expressao);
    }

    public List<Dimension> getColumnDimensionsUse(Cubo cubo) {
        return this.metricValueUse.getDimensoesColunaUtilizar(cubo);
    }

    public boolean testCondition(MapaMetricas mapaMetricas, MetricLine metricLine) {
        boolean result;
        Set<String> variables = this.filterExpression.getVariables().keySet();
        try {
            for (String variable : variables) {
                String tituloMetrica = this.filterExpression.getVariables().get(variable);
                Double valorMetrica = this.metricValueUse.getValor(mapaMetricas, metricLine, tituloMetrica);
                this.filterExpression.setValorVariable(variable, valorMetrica);
            }
            result = this.filterExpression.calculaValor() == 1;
        } catch (Exception e) {
            throw new CubeMathParserException("Não foi possível testar a condição do filtro métrica.", e);
        }
        return result;
    }

}
