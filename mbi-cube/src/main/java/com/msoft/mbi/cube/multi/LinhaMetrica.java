package com.msoft.mbi.cube.multi;

import java.util.Map;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import lombok.Getter;

@Getter
public class LinhaMetrica {

    private Dimension dimensionLinha;
    private Dimension dimensionColuna;
    private Map<String, Metrica> metricas;

    public LinhaMetrica(Dimension dimensionLinha, Dimension dimensionColuna, Map<String, Metrica> metricas) {
        this.dimensionLinha = dimensionLinha;
        this.dimensionColuna = dimensionColuna;
        this.metricas = metricas;
    }

    public LinhaMetrica getLinhaMetrica(Dimension dimensionLinha, Dimension dimensionColuna, Map<String, Metrica> metricas) {
        this.dimensionLinha = dimensionLinha;
        this.dimensionColuna = dimensionColuna;
        this.metricas = metricas;
        return this;
    }

}
