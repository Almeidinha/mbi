package com.msoft.mbi.cube.multi.resumeFunctions;

import java.util.List;
import java.util.Set;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricValueUse;

public class MetricFilters {

    protected Calculo filterExpression;
    private final MetricValueUse metricValueUse;

    public MetricFilters(String expressao, MetricValueUse metricValueUse) {
        this.metricValueUse = metricValueUse;
        expressao = "IF((" + expressao + "), 1, 0)";
        this.filterExpression = new Calculo(expressao);
    }

    public List<Dimension> getColumnDimensionsUse(Cube cube) {
        return this.metricValueUse.getDimensionColumnsUse(cube);
    }

    public boolean testCondition(MetricsMap metricsMap, MetricLine metricLine) {
        boolean result;
        Set<String> variables = this.filterExpression.getVariables().keySet();
        try {
            for (String variable : variables) {
                String tituloMetrica = this.filterExpression.getVariables().get(variable);
                Double valorMetrica = this.metricValueUse.getValor(metricsMap, metricLine, tituloMetrica);
                this.filterExpression.setValorVariable(variable, valorMetrica);
            }
            result = this.filterExpression.calculaValor() == 1;
        } catch (Exception e) {
            throw new CubeMathParserException("Não foi possível testar a condição do filtro métrica.", e);
        }
        return result;
    }

}
