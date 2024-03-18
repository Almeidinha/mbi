package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;

import java.io.IOException;
import java.io.Serial;
import java.io.Writer;

public class ImagemHTML extends ComponenteHTML {

    @Serial
    private static final long serialVersionUID = -2431930029409088846L;

    private String fonte = "";
    private int larguraBorda = 0;
    private String textoAlternativo = "";

    public ImagemHTML(String fonte) {
        this.fonte = fonte;
    }

    public ImagemHTML(String fonte, String altura, String largura) {
        this(fonte);
        this.altura = altura;
        this.largura = largura;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String newFonte) {
        fonte = newFonte;
    }

    public int getLarguraBorda() {
        return larguraBorda;
    }

    public void setLarguraBorda(int newLarguraBorda) {
        larguraBorda = newLarguraBorda;
    }

    public String getTextoAlternativo() {
        return textoAlternativo;
    }

    public void setTextoAlternativo(String newTextoAlternativo) {
        textoAlternativo = newTextoAlternativo;
    }

    public void montaComponente(Writer out) throws BIIOException {
        try {
            out.write("<img src=\"" + this.fonte + "\" ");
            if (!this.altura.isEmpty()) {
                out.write("height=\"" + this.altura + "\" ");
            }
            if (this.estilo != null) {
                out.write(this.estilo.toString());
            }
            if (!this.id.isEmpty()) {
                out.write("id=\"" + this.id + "\" ");
            }
            if (!this.largura.isEmpty()) {
                out.write("width=\"" + this.largura + "\" ");
            }
            if (!this.textoAlternativo.isEmpty()) {
                out.write("alt=\"" + this.textoAlternativo + "\" ");
            }
            out.write("border=\"" + this.larguraBorda + "\" ");
            out.write(">");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar c�clula da tabela.", e);
            bi.setErrorCode(BIIOException.ERRO_AO_ESCREVER_NO_BUFFER);
            bi.setLocal(this.getClass(), "montaComponente(Writer");
            throw bi;
        }
    }

}
