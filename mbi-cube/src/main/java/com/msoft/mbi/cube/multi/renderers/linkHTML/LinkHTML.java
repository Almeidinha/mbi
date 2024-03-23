package com.msoft.mbi.cube.multi.renderers.linkHTML;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LinkHTML {

    @Getter
    private String jsFunction;
    @Getter
    private final Map<String, String> parameters;
    private int width;

    public LinkHTML() {
        this.parameters = new HashMap<>();
    }

    public void addParameter(String parameter, String valor) {
        this.parameters.put(parameter, valor);
    }

    public void addParameter(String parameter) {
        this.parameters.put(parameter, null);
    }

    public List<String> getParameterValues() {
        return new ArrayList<>(parameters.values());
    }

    protected int getWidth() {
        return width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    public abstract String toString();

}
