package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.calculation.Calculo;

public class CalculoHierarquiaOrdenacao {

    private List<MetricaCalculadaMetaData> metricasCalculadas = new ArrayList<>();

    public CalculoHierarquiaOrdenacao(List<MetricaCalculadaMetaData> metricasCalculadas) {
        if (metricasCalculadas != null) {
            this.ordenaHierarquiaDeCalculo(metricasCalculadas);
        }
    }

    private void ordenaHierarquiaDeCalculo(List<MetricaCalculadaMetaData> metricasCalculadas) {

        Map<String, MetricaCalculadaMetaData> mapaMetricasCalculadas = new HashMap<>();
        for (MetricaCalculadaMetaData metrica : metricasCalculadas) {
            String titulo = metrica.getTitulo();
            mapaMetricasCalculadas.put(titulo, metrica);
        }

        for (MetricaCalculadaMetaData m : metricasCalculadas) {
            this.ordena(this.metricasCalculadas, mapaMetricasCalculadas, m);
        }

    }

    private void ordena(List<MetricaCalculadaMetaData> listaMetricasCalculadas, Map<String, MetricaCalculadaMetaData> mapaMetricasCalculadas, MetricaCalculadaMetaData metrica) {

        Calculo calculo = metrica.createCalculo();
        Iterator<String> variaveis = calculo.getVariables().keySet().iterator();
        while (variaveis.hasNext()) {
            String variavel = variaveis.next();
            String titulo = calculo.getVariables().get(variavel);
            MetricaCalculadaMetaData metricaInterna = mapaMetricasCalculadas.get(titulo);
            if (metricaInterna != null) {
                if (listaMetricasCalculadas.indexOf(metricaInterna) == -1) {
                    this.ordena(listaMetricasCalculadas, mapaMetricasCalculadas, metricaInterna);
                }
            }
        }
        if (listaMetricasCalculadas.indexOf(metrica) == -1) {
            listaMetricasCalculadas.add(metrica);
        }
    }

    public List<MetricaCalculadaMetaData> getMetricasCalculadasOrdenadas() {
        return metricasCalculadas;
    }

}
