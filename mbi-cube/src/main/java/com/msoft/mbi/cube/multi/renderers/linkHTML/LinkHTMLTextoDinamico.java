package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class LinkHTMLTextoDinamico extends LinkHTMLTexto {

    private String nomeParametroDinamico;

    public LinkHTMLTextoDinamico(String nomeParametroDinamico) {
        super(null);
        this.addParametro(nomeParametroDinamico);
        this.nomeParametroDinamico = nomeParametroDinamico;
    }

    public String getNomeParametroDinamico() {
        return nomeParametroDinamico;
    }

}
