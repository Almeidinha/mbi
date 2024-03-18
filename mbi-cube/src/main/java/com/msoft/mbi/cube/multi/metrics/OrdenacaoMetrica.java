package com.msoft.mbi.cube.multi.metrics;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public class OrdenacaoMetrica implements Comparable<OrdenacaoMetrica>, Serializable {

    @Serial
    private static final long serialVersionUID = 281148012185414803L;
    private int tipoOrdenacao = 1;
    private Integer sequenciaOrdenacao = -1;
    private String tituloMetrica;
    private MetricaValorUtilizar valorMetrica;

    public OrdenacaoMetrica(String sentidoOrdem, int sequenciaOrdenacao, String tituloMetrica, MetricaValorUtilizar metricaValorUtilizar) {
        this.tipoOrdenacao = "ASC".equals(sentidoOrdem) ? 1 : -1;
        this.sequenciaOrdenacao = sequenciaOrdenacao;
        this.tituloMetrica = tituloMetrica;
        this.valorMetrica = metricaValorUtilizar;
    }

    public int getTipoOrdenacao() {
        return this.tipoOrdenacao;
    }

    public Integer getSequenciaOrdenacao() {
        return sequenciaOrdenacao;
    }

    @Override
    public int compareTo(OrdenacaoMetrica o) {
        return this.sequenciaOrdenacao.compareTo(o.getSequenciaOrdenacao());
    }

    public String getTituloMetrica() {
        return tituloMetrica;
    }

    public Double calculaValorOrdenacao(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica) {
        return this.valorMetrica.getValor(mapaMetricas, linhaMetrica, this.getTituloMetrica());
    }

    public List<Dimension> getDimensoesColunaUtilizar(Cubo cubo) {
        return this.valorMetrica.getDimensoesColunaUtilizar(cubo);
    }
}
