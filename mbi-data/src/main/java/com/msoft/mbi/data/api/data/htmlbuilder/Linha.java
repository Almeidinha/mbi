package com.msoft.mbi.data.api.data.htmlbuilder;

import java.util.List;

public interface Linha {

    public void setEstilo(Object estilo, boolean isDimensao);

    public List<Celula> getCelulas();

    public boolean isAlertaAplicado();

    public void setAlertaAplicado(boolean alertaAplicado);

    public Object getEstiloAplicado();

    public void setEstiloAplicado(Object estilo);
}
