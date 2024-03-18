package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.StringWriter;
import java.io.Writer;

@Getter
@Setter
public abstract class ComponenteTabelaHTML extends ComponenteHTML {

    protected String alinhamento = "";
    protected String corFundo = "";
    protected String corBorda = "";
    protected String corBordaEscura = "";
    protected String corBordaClara = "";
    protected String alinhamentoVertical = "";
    protected String parametrosAdicionais = "";
    protected String editable = "";
    protected EstiloHTML estilo;

    public ComponenteTabelaHTML() {
    }

    protected String getPropriedades() {
        StringBuilder s = new StringBuilder();
        if (this.alinhamento != null && !this.alinhamento.isEmpty()) {
            s.append("align=\"").append(this.alinhamento).append("\" ");
        }
        if (this.corFundo != null && !"".equals(this.corFundo)) {
            s.append("bgcolor=\"");
            if (!this.corFundo.contains("#")) {
                s.append("#");
            }
            s.append(this.corFundo).append("\" ");
        }
        if (this.corBorda != null && !this.corBorda.isEmpty()) {
            s.append("borderColor=\"").append(this.corBorda).append("\" ");
        }
        if (this.corBordaEscura != null && !this.corBordaEscura.isEmpty()) {
            s.append("bordercolordark=\"").append(this.corBordaEscura).append("\" ");
        }
        if (this.corBordaClara != null && !this.corBordaClara.isEmpty()) {
            s.append("bordercolorlight=\"").append(this.corBordaClara).append("\" ");
        }
        if (this.classe != null && !this.classe.isEmpty()) {
            s.append("class=\"").append(this.classe).append("\" ");
        }
        if (this.id != null && !this.id.isEmpty()) {
            s.append("id='").append(this.id).append("' ");
        }
        if (this.alinhamentoVertical != null && !this.alinhamentoVertical.isEmpty()) {
            s.append("valign=\"").append(this.alinhamentoVertical).append("\" ");
        }
        if (this.parametrosAdicionais != null && !this.parametrosAdicionais.isEmpty()) {
            s.append(this.parametrosAdicionais);
        }
        if (this.altura != null && !this.altura.isEmpty()) {
            s.append("height=\"").append(this.altura).append("\" ");
        }
        if (this.largura != null && !this.largura.isEmpty()) {
            s.append("width=\"").append(this.largura).append("\" ");
        }
        if (this.editable != null && !this.editable.isEmpty()) {
            s.append("editable=\"").append(this.editable).append("\" ");
        }
        if (this.estilo != null) {
            s.append(this.estilo);
        }
        return s.toString();
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            this.montaComponente(writer);
        } catch (BIIOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public void toString(Writer out) throws BIIOException {
        this.montaComponente(out);
    }
}
