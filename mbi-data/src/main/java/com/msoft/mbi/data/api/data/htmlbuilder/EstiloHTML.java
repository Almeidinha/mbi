package com.msoft.mbi.data.api.data.htmlbuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstiloHTML implements Cloneable {

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

    public EstiloHTML() {
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

    public void setBorder(int largura, String estilo, String cor) {
        this.border = largura + "pt " + estilo + " " + cor;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("style=\"");
        if (this.backgroundColor != null && !this.backgroundColor.isEmpty()) {
            str.append("background-color: ");
            if (!this.backgroundColor.contains("#")) {
                str.append("#");
            }
            str.append(this.backgroundColor).append("; ");
        }
        if (this.border != null && !this.border.isEmpty()) {
            str.append("border: ").append(this.border).append("; ");
        }
        if (this.fontColor != null && !this.fontColor.isEmpty()) {
            str.append("color: ");
            if (!this.fontColor.contains("#")) {
                str.append("#");
            }
            str.append(this.fontColor).append("; ");
        }
        if (this.fontFamily != null && !this.fontFamily.isEmpty()) {
            str.append("font-family: ").append(this.fontFamily).append("; ");
        }
        if (this.fontSize != 0) {
            str.append("font-size: ").append(this.fontSize).append("px; ");
        }
        if (this.fontStyle != null && !this.fontStyle.isEmpty()) {
            str.append("font-style: ").append(this.fontStyle).append("; ");
        }
        if (this.fontWeight != null && !this.fontWeight.isEmpty()) {
            str.append("font-weight: ").append(this.fontWeight).append("; ");
        }
        if (this.textDecoration != null && !this.textDecoration.isEmpty()) {
            str.append("text-decoration: ").append(this.textDecoration).append("; ");
        }
        if (this.display != null && !this.display.isEmpty()) {
            str.append("display: ").append(this.display).append("; ");
        }
        if (this.position != null && !this.position.isEmpty()) {
            str.append("position: ").append(this.position).append("; ");
        }
        if (this.index != 0) {
            str.append("z-index: ").append(this.index).append("; ");
        }
        if (this.leftCoordinates != 0) {
            str.append("left: ").append(this.leftCoordinates).append("px; ");
        }
        if (this.rightCoordinates != 0) {
            str.append("top: ").append(this.rightCoordinates).append("; ");
        }
        if (this.height != null && !this.height.isEmpty()) {
            str.append("height: ").append(this.height).append("; ");
        }
        if (this.width != null && !this.width.isEmpty()) {
            str.append("width: ").append(this.width).append("; ");
        }
        if (this.additionalParameters != null && !this.additionalParameters.isEmpty()) {
            str.append(this.additionalParameters);
        }
        str.append("\"");
        return str.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EstiloHTML clone = (EstiloHTML) super.clone();
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
