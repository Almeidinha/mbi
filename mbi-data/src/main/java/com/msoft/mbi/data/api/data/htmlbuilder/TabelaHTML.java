package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TabelaHTML extends ComponenteTabelaHTML {

    private int larguraBorda;
    private String imagemFundo = "";
    private int cellpadding;
    private int cellspacing;
    private int cols;
    private String rules = "";
    private LinhaHTML linhaAtual;
    private int indiceLinhaAtual = -1;
    private List<LinhaHTML> linhas;
    private List<String> indice_linhas_novas = new ArrayList<>();

    public TabelaHTML() {
        this.linhas = new ArrayList<>();
    }

    public String getLargura() {
        return this.largura;
    }

    public void setLargura(String newLargura) {
        this.largura = newLargura;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String newAltura) {
        this.altura = newAltura;
    }

    public List<String> getLinhasNovas() {
        return indice_linhas_novas;
    }

    public void addLinhaNova(int index) {
        this.indice_linhas_novas.add(String.valueOf(index));
    }

    public void setLinhasNovas(List<String> list_linhas) {
        this.indice_linhas_novas = list_linhas;
    }

    public boolean eh_LinhaNova(int index) {
        if (indice_linhas_novas != null) {
            for (int i = 0; i < indice_linhas_novas.size(); i++) {
                if (Integer.parseInt(String.valueOf(indice_linhas_novas.get(i))) == index) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLinha(LinhaHTML linha) {
        this.linhas.add(linha);
        this.indiceLinhaAtual++;
        this.linhaAtual = linha;
    }

    public void removerLinha(LinhaHTML linha) {
        this.linhas.remove(linha);
    }

    public int getIndiceLinha(LinhaHTML linha) {
        if (this.linhaAtual.equals(linha))
            return this.indiceLinhaAtual;
        for (int i = 0; i < this.linhas.size(); i++) {
            if (linha.equals(this.linhas.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public LinhaHTML getLinhaInferior(LinhaHTML linha) {
        LinhaHTML retorno = null;
        for (int i = 0; i < this.linhas.size(); i++) {
            if (this.linhas.get(i) == linha) {
                if (i + 1 >= this.linhas.size()) {
                    retorno = null;
                } else {
                    retorno = (LinhaHTML) this.linhas.get(i + 1);
                }
                break;
            }
        }
        return retorno;
    }

    public LinhaHTML getLinhaSuperior(LinhaHTML linha) {
        LinhaHTML retorno = null;
        for (int i = 0; i < this.linhas.size(); i++) {
            if (this.linhas.get(i) == linha) {
                if (i - 1 < 0) {
                    retorno = null;
                } else {
                    retorno = (LinhaHTML) this.linhas.get(i - 1);
                }
                break;
            }
        }
        return retorno;
    }

    public void montaComponente(Writer out) throws BIIOException {
        try {
            out.write("<table ");
            out.write(this.getPropriedades());

            if (!this.imagemFundo.isEmpty()) {
                out.write("background=\"" + this.imagemFundo + "\" ");
            }
            if (this.cols != 0) {
                out.write("cols=\"" + this.cols + "\" ");
            }
            if (!this.rules.isEmpty()) {
                out.write("rules=\"" + this.rules + "\" ");
            }
            out.write(">\n");
            for (LinhaHTML linha : this.linhas) {
                linha.montaComponente(out);
            }
            out.write("</table>\n");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar céclula da tabela.", e);
            bi.setErrorCode(BIIOException.ERRO_AO_ESCREVER_NO_BUFFER);
            bi.setLocal(this.getClass(), "montaComponente(Writer");
            throw bi;
        }
    }

    public Object clone() {
        TabelaHTML tabela = new TabelaHTML();
        tabela.setAlinhamento(this.alinhamento);
        tabela.setAlinhamentoVertical(this.alinhamentoVertical);
        tabela.setAltura(this.altura);
        tabela.setClasse(this.classe);
        tabela.setCols(this.cols);
        tabela.setConteudo(this.conteudo);
        tabela.setCorBorda(this.corBorda);
        tabela.setCorBordaClara(this.corBordaClara);
        tabela.setCorBordaEscura(this.corBordaEscura);
        tabela.setCorFundo(this.corFundo);
        tabela.setEditable(this.editable);
        tabela.setEstilo(this.estilo);
        tabela.setId(this.id);
        tabela.setImagemFundo(this.imagemFundo);
        tabela.setLargura(this.largura);
        tabela.setLinhasNovas(this.indice_linhas_novas);
        tabela.setParametrosAdicionais(this.parametrosAdicionais);
        tabela.setRules(this.rules);
        tabela.setLinhas(this.linhas);
        return tabela;
    }
}
