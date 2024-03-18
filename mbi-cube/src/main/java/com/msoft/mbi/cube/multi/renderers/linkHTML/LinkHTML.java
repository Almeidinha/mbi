package com.msoft.mbi.cube.multi.renderers.linkHTML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LinkHTML {

    private String funcaoJS;
    private Map<String, String> parametros;
    private int width;

    public LinkHTML() {
        this.parametros = new HashMap<>();
    }

    public void addParametro(String parametro, String valor) {
        this.parametros.put(parametro, valor);
    }

    public void addParametro(String parametro) {
        this.parametros.put(parametro, null);
    }

    public String getFuncaoJS() {
        return funcaoJS;
    }

    public List<String> getValoresParametros() {
        return new ArrayList<String>(parametros.values());
    }

    public Map<String, String> getParametros() {
        return parametros;
    }

    protected int getWidth() {
        return width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    public abstract String toString();

}
