package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;

import java.io.IOException;
import java.io.Serial;
import java.io.Writer;

public class LinkHTML extends ComponenteHTML {

    @Serial
    private static final long serialVersionUID = 1L;
    private String href = "";
    private String nome = "";
    private String target = "";

    /**
     * Constructor
     */
    public LinkHTML(String href, Object conteudo) {
        this.href = href;
        this.conteudo = conteudo;
    }

    public LinkHTML(String href, Object conteudo, String classe) {
        this(href, conteudo);
        this.classe = classe;
    }

    public Object getConteudo() {
        return conteudo;
    }

    public void setConteudo(Object newConteudo) {
        this.conteudo = newConteudo;
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

    public void montaComponente(Writer out) throws BIIOException {
        try {
            out.write("<a href=\"" + this.href + "\" ");
            if (!this.classe.equals("")) {
                out.write("class=\"" + this.classe + "\" ");
            }
            if (this.estilo != null) {
                out.write("" + this.estilo + "");
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
            out.write(">" + this.conteudo + "</a>");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar céclula da tabela.", e);
            bi.setErrorCode(BIIOException.ERRO_AO_ESCREVER_NO_BUFFER);
            bi.setLocal(this.getClass(), "montaComponente(Writer");
            throw bi;
        }
    }
}
