package com.msoft.mbi.cube.multi.partialTotalization;

import java.io.Serializable;

import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public interface TotalizacaoParcialAplicarTipo extends Serializable {

    Double calculaValor(Dimension dimensionEixoReferencia, Dimension dimension, MetricaMetaData metaData, MapaMetricas mapaMetricas);

}
