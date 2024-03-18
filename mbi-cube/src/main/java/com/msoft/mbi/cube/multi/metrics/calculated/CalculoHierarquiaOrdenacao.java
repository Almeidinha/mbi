package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.calculation.Calculo;

public class CalculoHierarquiaOrdenacao {

    private final List<MetricCalculatedMetaData> metricasCalculadas = new ArrayList<>();

    public CalculoHierarquiaOrdenacao(List<MetricCalculatedMetaData> metricasCalculadas) {
        if (metricasCalculadas != null) {
            this.ordenaHierarquiaDeCalculo(metricasCalculadas);
        }
    }

    private void ordenaHierarquiaDeCalculo(List<MetricCalculatedMetaData> metricasCalculadas) {

        Map<String, MetricCalculatedMetaData> mapaMetricasCalculadas = new HashMap<>();
        for (MetricCalculatedMetaData metrica : metricasCalculadas) {
            String titulo = metrica.getTitle();
            mapaMetricasCalculadas.put(titulo, metrica);
        }

        for (MetricCalculatedMetaData m : metricasCalculadas) {
            this.ordena(this.metricasCalculadas, mapaMetricasCalculadas, m);
        }

    }

    private void ordena(List<MetricCalculatedMetaData> listaMetricasCalculadas, Map<String, MetricCalculatedMetaData> mapaMetricasCalculadas, MetricCalculatedMetaData metrica) {

        Calculo calculo = metrica.createCalculo();
        for (String variavel : calculo.getVariables().keySet()) {
            String titulo = calculo.getVariables().get(variavel);
            MetricCalculatedMetaData metricaInterna = mapaMetricasCalculadas.get(titulo);
            if (metricaInterna != null) {
                if (!listaMetricasCalculadas.contains(metricaInterna)) {
                    this.ordena(listaMetricasCalculadas, mapaMetricasCalculadas, metricaInterna);
                }
            }
        }
        if (!listaMetricasCalculadas.contains(metrica)) {
            listaMetricasCalculadas.add(metrica);
        }
    }

    public List<MetricCalculatedMetaData> getMetricasCalculadasOrdenadas() {
        return metricasCalculadas;
    }

}
