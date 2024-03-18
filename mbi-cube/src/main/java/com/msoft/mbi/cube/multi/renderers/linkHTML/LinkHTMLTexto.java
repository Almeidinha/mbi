package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class LinkHTMLTexto extends LinkHTML {

    private String conteudo;

    public LinkHTMLTexto(String conteudo) {
        super();
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        StringBuilder sLink = new StringBuilder();
        sLink.append("<span");

        for (String param : this.getParametros().keySet()) {
            sLink.append(" ").append(param).append("='").append(this.getParametros().get(param)).append("'");
        }
        sLink.append(">").append(this.conteudo).append("</span>");
        return sLink.toString();
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
