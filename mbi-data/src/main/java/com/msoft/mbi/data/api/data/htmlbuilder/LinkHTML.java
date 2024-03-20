package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;
import java.io.IOException;
import java.io.Writer;

public class LinkHTML extends HTMLComponent {

    private String href = "";
    private String nome = "";
    private String target = "";

    /**
     * Constructor
     */
    public LinkHTML(String href, Object conteudo) {
        this.href = href;
        this.content = conteudo;
    }

    public LinkHTML(String href, Object conteudo, String classe) {
        this(href, conteudo);
        this.cellClass = classe;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object newConteudo) {
        this.content = newConteudo;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String newHref) {
        href = newHref;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String newNome) {
        nome = newNome;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String newTarget) {
        target = newTarget;
    }

    public void buildComponent(Writer out) throws BIIOException {
        try {
            out.write("<a href=\"" + this.href + "\" ");
            if (!this.cellClass.equals("")) {
                out.write("class=\"" + this.cellClass + "\" ");
            }
            if (this.style != null) {
                out.write("" + this.style + "");
            }
            if (!this.id.equals("")) {
                out.write("id=\"" + this.id + "\" ");
            }
            if (!this.nome.equals("")) {
                out.write("name=\"" + this.nome + "\" ");
            }
            if (!this.target.equals("")) {
                out.write("target=\"" + this.target + "\" ");
            }
            out.write(">" + this.content + "</a>");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar céclula da tabela.", e);
            bi.setErrorCode(BIIOException.ERRO_AO_ESCREVER_NO_BUFFER);
            bi.setLocal(this.getClass(), "montaComponente(Writer");
            throw bi;
        }
    }
}
