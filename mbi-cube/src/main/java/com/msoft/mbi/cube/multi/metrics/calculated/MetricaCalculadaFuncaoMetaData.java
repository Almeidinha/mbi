package com.msoft.mbi.cube.multi.metrics.calculated;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public interface MetricaCalculadaFuncaoMetaData {

    public String getFuncaoCampo();

    public String getTituloCampoReferencia();

    public AnaliseParticipacaoTipo getAnaliseParticipacaoTipo();

    public Dimension getDimensaoEixoReferencia(LinhaMetrica linhaMetrica);

    public Dimension getDimensaoOutra(LinhaMetrica linhaMetrica);

}
