package com.msoft.mbi.cube.multi.metaData;

import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTML;

public class MascaraLinkHTMLMetaData {

    private String idMascara;
    private int tipo;
    private LinkHTML linkHTML;
    public static final int TIPO_DEPOIS_VALOR = 1;
    public static final int TIPO_VALOR = 2;
    public static final int TIPO_DINAMICO = 3;

    public MascaraLinkHTMLMetaData(String idMascara, int tipo, LinkHTML linkHTML) {
        this.idMascara = idMascara;
        this.linkHTML = linkHTML;
        this.tipo = tipo;
    }

    public int getTipo() {
        return tipo;
    }

    public LinkHTML getLinkHTML() {
        return linkHTML;
    }

    public String getIdMascara() {
        return idMascara;
    }

    public void setLinkHTML(LinkHTML linkHTML) {
        this.linkHTML = linkHTML;
    }

}