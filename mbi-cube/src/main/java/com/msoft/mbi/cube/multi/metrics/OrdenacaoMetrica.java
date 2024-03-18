package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import lombok.Getter;

public class OrdenacaoMetrica implements Comparable<OrdenacaoMetrica> {

    @Getter
    private final int tipoOrdenacao;
    @Getter
    private final Integer sequenciaOrdenacao;
    @Getter
    private final String tituloMetrica;
    private final MetricaValorUtilizar valorMetrica;

    public OrdenacaoMetrica(String sentidoOrdem, int sequenciaOrdenacao, String tituloMetrica, MetricaValorUtilizar metricaValorUtilizar) {
        this.tipoOrdenacao = "ASC".equals(sentidoOrdem) ? 1 : -1;
        this.sequenciaOrdenacao = sequenciaOrdenacao;
        this.tituloMetrica = tituloMetrica;
        this.valorMetrica = metricaValorUtilizar;
    }

    @Override
    public int compareTo(OrdenacaoMetrica o) {
        return this.sequenciaOrdenacao.compareTo(o.getSequenciaOrdenacao());
    }

    public Double calculaValorOrdenacao(MapaMetricas mapaMetricas, MetricLine metricLine) {
        return this.valorMetrica.getValor(mapaMetricas, metricLine, this.getTituloMetrica());
    }

    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo) {
        return this.valorMetrica.getDimensoesColunaUtilizar(cubo);
    }
}
