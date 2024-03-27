package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.exception.BIIOException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.StringWriter;
import java.io.Writer;

@Log4j2
@Setter
@Getter
public abstract class HTMLComponent {


    protected String id = "";
    protected String cellClass = "";
    protected HTMLStyle style;
    protected Object content;
    protected String height = "";
    protected String width = "";

    public HTMLComponent() {
    }

    protected String getProperties() {
        StringBuilder s = new StringBuilder();

        appendAttributeIfNotEmpty(s, "class", this.cellClass);
        appendAttributeIfNotEmpty(s, "id", this.id);
        appendAttribute(s, String.valueOf(this.style));
        appendAttributeIfNotEmpty(s, "height", this.height);
        appendAttributeIfNotEmpty(s, "width", this.width);

        return s.toString();
    }

    private void appendAttributeIfNotEmpty(StringBuilder s, String attributeName, String attributeValue) {
        if (!attributeValue.isEmpty()) {
            s.append(attributeName).append("=\"").append(attributeValue).append("\" ");
        }
    }

    private void appendAttribute(StringBuilder s, String attribute) {
        if (attribute != null) {
            s.append(attribute);
        }
    }

    public abstract void buildComponent(Writer out) throws BIIOException;

    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            this.buildComponent(writer);
        } catch (BIIOException e) {
            log.error("Error in ComponentHTML.buildComponent()" + e.getMessage());
        }
        return writer.toString();
    }

    public void toString(Writer out) throws BIIOException {
        this.buildComponent(out);
    }
}
