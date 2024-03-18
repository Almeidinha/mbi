package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.StringWriter;
import java.io.Writer;

@Setter
@Getter
public abstract class ComponenteHTML {


    protected String id = "";
    protected String classe = "";
    protected EstiloHTML estilo;
    protected Object conteudo;
    protected String altura = "";
    protected String largura = "";

    public ComponenteHTML() {
    }

    protected String getPropriedades() {
        StringBuilder s = new StringBuilder();
        if (!this.classe.isEmpty()) {
            s.append("class=\"").append(this.classe).append("\" ");
        }
        if (!this.id.isEmpty()) {
            s.append("id=\"").append(this.id).append("\" ");
        }
        if (this.estilo != null) {
            s.append(this.estilo);
        }
        if (!this.altura.isEmpty()) {
            s.append("height=\"").append(this.altura).append("\" ");
        }
        if (!this.largura.isEmpty()) {
            s.append("width=\"").append(this.largura).append("\" ");
        }
        return s.toString();
    }

    public abstract void montaComponente(Writer out) throws BIIOException;

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
