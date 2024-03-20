package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;

@Setter
@Getter
public class HTMLImage extends HTMLComponent {

    private String font = "";
    private int borderWidth = 0;
    private String alternativeText = "";

    public HTMLImage(String font) {
        this.font = font;
    }

    public HTMLImage(String font, String height, String width) {
        this(font);
        this.height = height;
        this.width = width;
    }

    public void buildComponent(Writer out) throws BIIOException {
        try {
            out.write("<img src=\"" + this.font + "\" ");
            writeAttributeIfNotEmpty(out, "height", this.height);
            writeAttribute(out, String.valueOf(this.style));
            writeAttributeIfNotEmpty(out, "id", this.id);
            writeAttributeIfNotEmpty(out, "width", this.width);
            writeAttributeIfNotEmpty(out, "alt", this.alternativeText);
            out.write("border=\"" + this.borderWidth + "\" ");
            out.write(">");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar célula da tabela.", e);
            bi.setErrorCode(BIIOException.ERRO_AO_ESCREVER_NO_BUFFER);
            bi.setLocal(this.getClass(), "buildComponent(Writer)");
            throw bi;
        }
    }

    private void writeAttributeIfNotEmpty(Writer out, String attributeName, String attributeValue) throws IOException {
        if (!attributeValue.isEmpty()) {
            out.write(attributeName + "=\"" + attributeValue + "\" ");
        }
    }

    private void writeAttribute(Writer out, String attribute) throws IOException {
        if (attribute != null) {
            out.write(attribute);
        }
    }

}
