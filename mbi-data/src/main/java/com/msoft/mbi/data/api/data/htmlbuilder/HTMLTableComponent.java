package com.msoft.mbi.data.api.data.htmlbuilder;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public abstract class HTMLTableComponent extends HTMLComponent {

    protected String alignment = "";
    protected String backGroundColor = "";
    protected String borderColor = "";
    protected String darkBorderColor = "";
    protected String lightBorderColor = "";
    protected String verticalAlignment = "";
    protected String additionalParameters = "";
    protected String editable = "";
    protected HTMLStyle style;

    public HTMLTableComponent() {
    }

    protected String getProperties() {
        StringBuilder s = new StringBuilder();

        appendProperty(s, "align", alignment);
        appendProperty(s, "bgcolor", formatColor(backGroundColor));
        appendProperty(s, "borderColor", borderColor);
        appendProperty(s, "bordercolordark", darkBorderColor);
        appendProperty(s, "bordercolorlight", lightBorderColor);
        appendProperty(s, "class", cellClass);
        appendProperty(s, "id", id, true);
        appendProperty(s, "valign", verticalAlignment);
        appendProperty(s, "height", height);
        appendProperty(s, "width", width);
        appendProperty(s, "editable", editable);

        // Directly append additionalParameters and style if not null or empty
        Optional.ofNullable(additionalParameters).ifPresent(s::append);
        Optional.ofNullable(style).ifPresent(s::append);

        return s.toString();
    }

    private void appendProperty(StringBuilder builder, String propertyName, String propertyValue) {
        appendProperty(builder, propertyName, propertyValue, false);
    }

    private void appendProperty(StringBuilder builder, String propertyName, String propertyValue, boolean useSingleQuotes) {
        if (propertyValue != null && !propertyValue.isEmpty()) {
            builder.append(propertyName).append(useSingleQuotes ? "=`" : "=\"").append(propertyValue).append(useSingleQuotes ? "` " : "\" ");
        }
    }

    private String formatColor(String color) {
        if (color != null && !color.isEmpty() && !color.startsWith("#")) {
            return "#" + color;
        }
        return color;
    }
}
