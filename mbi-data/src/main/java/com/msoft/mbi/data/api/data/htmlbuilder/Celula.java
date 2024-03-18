package com.msoft.mbi.data.api.data.htmlbuilder;

public interface Celula {


    public boolean isAlertaAplicado();

    public void setAlertaAplicado(boolean alertaAplicado);

    public boolean isCelulaTH();

    public boolean isDimensaoColuna();

    public void setDimensaoColuna(boolean dimensaoColuna);

    public boolean isExcel();

    public void setEstilo(Object estilo);

    public Object getConteudo();

    public void setClasse(String classe);
}
