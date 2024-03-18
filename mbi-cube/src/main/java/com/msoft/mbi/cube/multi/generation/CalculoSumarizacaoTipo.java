package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public interface CalculoSumarizacaoTipo {


    String NORMAL = "normal";
    String MEDIA = "media";
    String TOTAL = "total";

    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricaMetaData metaData, String tipoLinha);

}
