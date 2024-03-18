package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarLinhaMetrica;

import java.io.Serial;

public class MetricaCalculada extends Metrica {


    @Override
    public MetricaCalculadaMetaData getMetaData() {
        return (MetricaCalculadaMetaData) super.getMetaData();
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.agregador.getValorAgregado();
    }

    public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar nivelCalcular) {
        MetricaCalculadaMetaData metaData = this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno;
        try {
            for (String variable : calculo.getVariables().keySet()) {
                Metrica expressao = metricLine.getMetrics().get(calculo.getVariables().get(variable));
                if (expressao == null) {
                    expressao = metricLine.getMetrics().get(this.removeBarraVariable(calculo.getVariables().get(variable)));
                }

                Double valor = nivelCalcular.calculaValor(expressao, metricLine, mapaMetricas);
                calculo.setValorVariable(variable, (valor != null ? valor : 0));
            }
            retorno = calculo.calculaValor();
        } catch (Exception ex) {
            throw new CubeMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
        }
        return retorno;
    }

    public String removeBarraVariable(String variable) {
        return variable.replaceAll("\\\\", "");
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {
        return this.calcula(mapaMetricas, metricLine, metricLineAnterior, MetricaValorUtilizarLinhaMetrica.getInstance());
    }

    public void populaNovoValor(Double valor) {
        this.getMetaData().getAgregacaoAplicarOrdem().populaNovoValor(this, valor);
    }

    public void agregaValor(Double valor) {
        this.agregador.agregaValor(valor);
    }

    public void setValor(Double valor) {
        this.agregador.setValor(valor);
    }

}
