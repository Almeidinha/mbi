package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.exception.CuboMathParserException;
import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarLinhaMetrica;

import java.io.Serial;

public class MetricaCalculada extends Metrica {

    @Serial
    private static final long serialVersionUID = 3920289781688248321L;

    @Override
    public MetricaCalculadaMetaData getMetaData() {
        return (MetricaCalculadaMetaData) super.getMetaData();
    }

    @Override
    public Double getValor(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
        return this.agregador.getValorAgregado();
    }

    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior, MetricaValorUtilizar nivelCalcular) {
        MetricaCalculadaMetaData metaData = this.getMetaData();
        Calculo calculo = metaData.createCalculo();
        Double retorno;
        try {
            for (String variable : calculo.getVariables().keySet()) {
                Metrica expressao = linhaMetrica.getMetricas().get(calculo.getVariables().get(variable));
                if (expressao == null) {
                    expressao = linhaMetrica.getMetricas().get(this.removeBarraVariable(calculo.getVariables().get(variable)));
                }

                Double valor = nivelCalcular.calculaValor(expressao, linhaMetrica, mapaMetricas);
                calculo.setValorVariable(variable, (valor != null ? valor : 0));
            }
            retorno = calculo.calculaValor();
        } catch (Exception ex) {
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível realizar o cálculo da coluna " + metaData.getTitulo() + ".", ex);
            throw parserException;
        }
        return retorno;
    }

    public String removeBarraVariable(String variable) {
        return variable.replaceAll("\\\\", "");
    }

    @Override
    public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
        return this.calcula(mapaMetricas, linhaMetrica, linhaMetricaAnterior, MetricaValorUtilizarLinhaMetrica.getInstance());
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
