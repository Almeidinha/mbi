package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.exception.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;

@Setter
@Getter
public class LinkHTML extends HTMLComponent {

    private String href = "";
    private String nome = "";
    private String target = "";

    public LinkHTML(String href, Object content) {
        this.href = href;
        this.content = content;
    }

    public LinkHTML(String href, Object content, String cellClass) {
        this(href, content);
        this.cellClass = cellClass;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public void buildComponent(Writer out) throws BIIOException {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<a href=\"").append(this.href).append("\" ");
            appendAttribute(html, "class", this.cellClass);
            appendAttribute(html, "style", this.style == null ? "" : this.style.toString());
            appendAttribute(html, "id", this.id);
            appendAttribute(html, "name", this.nome);
            appendAttribute(html, "target", this.target);
            html.append(">").append(this.content).append("</a>");

            out.write(html.toString());
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar céclula da tabela.", e);
            bi.setErrorCode(BIIOException.BUFFER_WRITE_ERROR);
            bi.setLocal(this.getClass(), "buildComponent(Writer");
            throw bi;
        }
    }

    private void appendAttribute(StringBuilder html, String attributeName, String attributeValue) {
        if (attributeValue != null && !attributeValue.isEmpty()) {
            html.append(attributeName).append("=\"").append(attributeValue).append("\" ");
        }
    }
}
