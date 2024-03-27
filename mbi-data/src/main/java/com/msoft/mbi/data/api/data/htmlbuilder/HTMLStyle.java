package com.msoft.mbi.data.api.data.htmlbuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class HTMLStyle implements Cloneable {

    private String borderColor = "";
    private String borderLeftColor = "";
    private String borderRightColor = "";
    private String borderTopColor = "";
    private String borderBottomColor = "";
    private String border = "";
    private String backgroundColor = "";
    private int fontSize;
    private String fontFamily = "";
    private String fontColor = "";
    private String fontWeight = "";
    private String fontStyle = "";
    private String textDecoration = "";
    private String additionalParameters = "";
    private String display = "";
    private String position = "";
    private String width = "";
    private String height = "";
    private int index;
    private int leftCoordinates;
    private int rightCoordinates;

    public HTMLStyle() {
    }

    public String getBackgroundColor() {
        if (!this.backgroundColor.contains("#")) {
            this.backgroundColor = "#" + this.backgroundColor;
        }
        return backgroundColor;
    }

    public String getFontColor() {
        if (!this.fontColor.contains("#")) {
            this.fontColor = "#" + this.fontColor;
        }
        return fontColor;
    }

    public void setBorder(int width, String style, String color) {
        this.border = width + "pt " + style + " " + color;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        appendStyleAttribute(str, "background-color", backgroundColor);
        appendStyleAttribute(str, "border", border);
        appendStyleAttribute(str, "color", fontColor);
        appendStyleAttribute(str, "font-family", fontFamily);
        appendStyleAttribute(str, "font-size", fontSize + "px");
        appendStyleAttribute(str, "font-style", fontStyle);
        appendStyleAttribute(str, "font-weight", fontWeight);
        appendStyleAttribute(str, "text-decoration", textDecoration);
        appendStyleAttribute(str, "display", display);
        appendStyleAttribute(str, "position", position);
        appendStyleAttribute(str, "z-index", Integer.toString(index));
        appendStyleAttribute(str, "left", leftCoordinates + "px");
        appendStyleAttribute(str, "top", rightCoordinates + "px");
        appendStyleAttribute(str, "height", height);
        appendStyleAttribute(str, "width", width);
        if (additionalParameters != null && !additionalParameters.isEmpty()) {
            str.append(additionalParameters);
        }
        return str.toString();
    }

    private void appendStyleAttribute(StringBuilder str, String attributeName, String attributeValue) {
        if (attributeValue != null && !attributeValue.isEmpty()) {
            if (!attributeValue.contains("#") && attributeName.equals("color") || attributeName.equals("background-color")) {
                attributeValue = "#" + attributeValue;
            }
            str.append(attributeName).append(": ").append(attributeValue).append("; ");
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        HTMLStyle clone = (HTMLStyle) super.clone();
        clone.setHeight(this.height);
        clone.setBorderColor(this.borderColor);
        clone.setBorderLeftColor(this.borderLeftColor);
        clone.setBorderRightColor(this.borderRightColor);
        clone.setBorderTopColor(this.borderTopColor);
        clone.setBorderBottomColor(this.borderBottomColor);
        clone.setBackgroundColor(this.backgroundColor);
        clone.setLeftCoordinates(this.leftCoordinates);
        clone.setRightCoordinates(this.rightCoordinates);
        clone.setDisplay(this.display);
        clone.setFontColor(this.fontColor);
        clone.setFontFamily(this.fontFamily);
        clone.setFontSize(this.fontSize);
        clone.setFontStyle(this.fontStyle);
        clone.setFontWeight(this.fontWeight);
        clone.setIndex(this.index);
        clone.setWidth(this.width);
        clone.setAdditionalParameters(this.additionalParameters);
        clone.setPosition(this.position);
        clone.setTextDecoration(this.textDecoration);
        return clone;
    }
}
