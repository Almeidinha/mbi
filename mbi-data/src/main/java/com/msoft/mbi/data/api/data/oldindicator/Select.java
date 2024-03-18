package com.msoft.mbi.data.api.data.oldindicator;

import lombok.Getter;
import lombok.Setter;

public class Select {
    private final StringBuilder elements;
    private String start;
    private String end;
    @Setter
    private String componentParameter;
    @Setter
    private String fieldParameters = "";
    @Setter
    private String name;
    private boolean emptyField = false;
    @Setter
    private int size;
    @Setter
    private boolean active = true;

    @Getter
    @Setter
    private boolean multipleSelection = false;


    public Select() {
        elements = new StringBuilder();
    }

    public void addEmptyField(boolean cmpVazio) {
        this.emptyField = cmpVazio;
    }

    protected void setInicioComponente() {
        start = "<select name='" + name + "' id='" + name + "' size='" + size + "' " + componentParameter + " " + (active ? "enabled" : "disabled")
                + (multipleSelection ? " multiple" : "") + ">";
        if (this.emptyField) {
            start += "<option value=\"\">&nbsp;</option>";
        }
        end = "</select>";
    }

    public void addElement(Object valor, Object rotulo, boolean selecionado) {
        addElement(valor, rotulo, selecionado, "");
    }

    public void addElement(Object valor, Object rotulo, boolean selecionado, String classe) {
        elements.append("<option value='").append(valor).append("' ").append((selecionado ? " selected " : " "));
        if (!classe.isEmpty()) {
            elements.append("class='").append(classe).append("' ");
        }
        elements.append((fieldParameters == null ? "" : fieldParameters)).append(" title='").append(rotulo).append("'>").append(rotulo);
        elements.append("</option>\n");
    }

    public String toString() {
        this.setInicioComponente();
        if (start != null && !start.isEmpty())
            return start + elements.toString() + end;
        else
            return "";
    }

}
