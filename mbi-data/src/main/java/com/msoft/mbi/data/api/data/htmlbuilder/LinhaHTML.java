package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.util.BIIOException;
import lombok.Setter;

import java.io.IOException;
import java.io.Serial;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinhaHTML extends ComponenteTabelaHTML implements Linha {

    @Serial
    private static final long serialVersionUID = 1L;
    @Setter
    private List<Celula> celulas;
    private CelulaHTML celulaAtual;

    @Setter
    private boolean alertaAplicado = false;

    public LinhaHTML() {
        this.celulas = new ArrayList<>();
    }

    public void addCelula(CelulaHTML celula) {
        this.celulas.add(celula);
        this.celulaAtual = celula;
    }

    public List<Celula> getCelulas() {
        return this.celulas;
    }

    public CelulaHTML getCelulaAtual() {
        return this.celulaAtual;
    }

    public CelulaHTML getCelulaPosterior(CelulaHTML celula) {
        CelulaHTML retorno = null;
        for (int i = 0; i < this.celulas.size(); i++) {
            if (this.celulas.get(i) == celula) {
                if (i + 1 >= this.celulas.size()) {
                    retorno = null;
                } else {
                    retorno = (CelulaHTML) this.celulas.get(i + 1);
                }
                break;
            }
        }
        return retorno;
    }

    public CelulaHTML getCelulaAnterior(CelulaHTML celula) {
        CelulaHTML retorno = null;
        for (int i = 0; i < this.celulas.size(); i++) {
            if (this.celulas.get(i) == celula) {
                if (i - 1 < 0) {
                    retorno = null;
                } else {
                    retorno = (CelulaHTML) this.celulas.get(i - 1);
                }
                break;
            }
        }
        return retorno;
    }

    public void montaComponente(Writer out) throws BIIOException {
        try {
            out.write("<tr ");
            out.write(this.getPropriedades());
            out.write(">\n");
            Iterator<Celula> i = this.celulas.iterator();
            CelulaHTML celula;
            while (i.hasNext()) {
                celula = (CelulaHTML) i.next();
                celula.toString(out);
            }
            out.write("</tr>\n");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar céclula da tabela.", e);
            bi.setErrorCode(BIIOException.ERRO_AO_ESCREVER_NO_BUFFER);
            bi.setLocal(this.getClass(), "montaComponente(Writer");
            throw bi;
        }
    }

    public Object clone() {
        LinhaHTML linha;
        linha = new LinhaHTML();
        linha.setAlinhamento(this.getAlinhamento());
        linha.setAlinhamentoVertical(this.getAlinhamentoVertical());
        linha.setClasse(this.getClasse());
        linha.setCorBorda(this.getCorBorda());
        linha.setCorBordaClara(this.getCorBordaClara());
        linha.setCorBordaEscura(this.getCorBordaEscura());
        linha.setCorFundo(this.getCorFundo());
        linha.setEditable(this.getEditable());
        linha.setEstilo(this.getEstilo());
        linha.setId(this.getId());
        linha.setParametrosAdicionais(this.parametrosAdicionais);
        linha.setAltura(this.altura);
        linha.setLargura(this.largura);
        linha.setCelulas(this.celulas);
        return linha;
    }

    public CelulaHTML getPrimeiraCelula() {
        CelulaHTML retorno = null;
        if (!this.getCelulas().isEmpty()) {
            retorno = (CelulaHTML) this.getCelulas().get(0);
        }
        return retorno;
    }

    public int getMaiorRowspanLinha() {
        int retorno = 0;
        for (Celula celula : this.celulas) {
            CelulaHTML celAux = (CelulaHTML) celula;
            if (celAux.getRowspan() > retorno) {
                retorno = celAux.getRowspan();
            }
        }
        return retorno;
    }

    public void setEstilo(Object estilo) {
        this.setEstilo((EstiloHTML) estilo);
    }

    public void setEstilo(Object estilo, boolean isMetricaAplicando) {
        this.setEstilo(estilo);
    }

    public boolean isAlertaAplicado() {
        return this.alertaAplicado;
    }


    public Object getEstiloAplicado() {
        return null;
    }

    public void setEstiloAplicado(Object estiloAplicado) {
    }

    public void zeraEstiloCelulas() {
        for (Celula value : this.getCelulas()) {
            CelulaHTML celula = (CelulaHTML) value;
            if (celula != null) {
                if (!celula.isAlertaAplicado() && !celula.isDimensaoColuna()) {
                    celula.setEstilo(null);
                }
            }
        }
    }
}
