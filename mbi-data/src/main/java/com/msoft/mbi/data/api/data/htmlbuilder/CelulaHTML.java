package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;

@Getter
@Setter
public class CelulaHTML extends ComponenteTabelaHTML implements Celula {

    private int colspan;
    private int rowspan;
    private String imagemFundo = "";
    private boolean nowrap;
    private Object conteudo = "";
    private boolean alertaAplicado = false;
    private boolean dimensaoColuna = false;
    private boolean isCelulaTH = false;

    public CelulaHTML() {
    }

    public String getLargura() {
        return this.largura;
    }

    public void setLargura(String largura) {
        this.largura = largura;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public void montaComponente(Writer out) throws BIIOException {
        try {
            if (isCelulaTH) {
                out.write("<th ");
            } else {
                out.write("<td ");
            }
            out.write(this.getPropriedades());

            if (!this.imagemFundo.isEmpty()) {
                out.write("background=\"" + this.imagemFundo + "\" ");
            }
            if (this.colspan != 0) {
                out.write("colspan=\"" + this.colspan + "\" ");
            }
            if (this.rowspan != 0) {
                out.write("rowspan=\"" + this.rowspan + "\" ");
            }
            if (this.nowrap) {
                out.write(" nowrap");
            }
            if (isCelulaTH) {
                out.write(">" + this.conteudo + "</th>\n");
            } else {
                out.write(">" + this.conteudo + "</td>\n");
            }
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar céclula da tabela.", e);
            bi.setErrorCode(BIIOException.ERRO_AO_ESCREVER_NO_BUFFER);
            bi.setLocal(this.getClass(), "montaComponente(Writer");
            throw bi;
        }
    }

    public Object clone() {
        CelulaHTML celula;
        celula = new CelulaHTML();
        celula.setAlinhamento(this.getAlinhamento());
        celula.setAlinhamentoVertical(this.getAlinhamentoVertical());
        celula.setAltura(this.getAltura());
        celula.setClasse(this.getClasse());
        celula.setColspan(this.getColspan());
        celula.setConteudo(this.getConteudo());
        celula.setCorBorda(this.getCorBorda());
        celula.setCorBordaClara(this.getCorBordaClara());
        celula.setCorBordaEscura(this.getCorBordaEscura());
        celula.setCorFundo(this.getCorFundo());
        celula.setEditable(this.getEditable());
        celula.setEstilo(this.getEstilo());
        celula.setId(this.getId());
        celula.setImagemFundo(this.getImagemFundo());
        celula.setLargura(this.getLargura());
        celula.setNowrap(this.isNowrap());
        celula.setParametrosAdicionais(this.parametrosAdicionais);
        celula.setRowspan(this.getRowspan());
        return celula;
    }

    public boolean isExcel() {
        return false;
    }

    public void setEstilo(Object estilo) {
        this.setEstilo((EstiloHTML) estilo);
    }


}
