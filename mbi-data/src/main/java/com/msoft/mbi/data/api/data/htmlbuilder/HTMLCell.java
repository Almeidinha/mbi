package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.exception.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;

@Getter
@Setter
public class HTMLCell extends HTMLTableComponent implements Cell {

    private int colspan;
    private int rowspan;
    private String backGroundImage = "";
    private boolean nowrap;
    private Object content = "";
    private boolean appliedAlert = false;
    private boolean dimensionColumn = false;
    private boolean THCell = false;

    public HTMLCell() {
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void buildComponent(Writer out) throws BIIOException {
        try {
            if (THCell) {
                out.write("<th ");
            } else {
                out.write("<td ");
            }
            out.write(this.getProperties());

            writeAttributeIfNotEmpty(out, "background", this.backGroundImage);
            writeAttributeIfNotZero(out, "colspan", this.colspan);
            writeAttributeIfNotZero(out, "rowspan", this.rowspan);
            writeAttributeIfTrue(out, "nowrap", this.nowrap);

            out.write(">" + this.content);
            out.write(THCell ? "</th>\n" : "</td>\n");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar célula da tabela.", e);
            bi.setErrorCode(BIIOException.BUFFER_WRITE_ERROR);
            bi.setLocal(this.getClass(), "buildComponent(Writer)");
            throw bi;
        }
    }

    private void writeAttributeIfNotEmpty(Writer out, String attributeName, String attributeValue) throws IOException {
        if (!attributeValue.isEmpty()) {
            out.write(attributeName + "=\"" + attributeValue + "\" ");
        }
    }

    private void writeAttributeIfNotZero(Writer out, String attributeName, int attributeValue) throws IOException {
        if (attributeValue != 0) {
            out.write(attributeName + "=\"" + attributeValue + "\" ");
        }
    }

    private void writeAttributeIfTrue(Writer out, String attributeName, boolean condition) throws IOException {
        if (condition) {
            out.write(attributeName);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        HTMLCell cell = (HTMLCell)super.clone();
        cell.setAlignment(this.getAlignment());
        cell.setVerticalAlignment(this.getVerticalAlignment());
        cell.setHeight(this.getHeight());
        cell.setCellClass(this.getCellClass());
        cell.setColspan(this.getColspan());
        cell.setContent(this.getContent());
        cell.setBorderColor(this.getBorderColor());
        cell.setLightBorderColor(this.getLightBorderColor());
        cell.setDarkBorderColor(this.getDarkBorderColor());
        cell.setBackGroundColor(this.getBackGroundColor());
        cell.setEditable(this.getEditable());
        cell.setStyle(this.getStyle());
        cell.setId(this.getId());
        cell.setBackGroundImage(this.getBackGroundImage());
        cell.setWidth(this.getWidth());
        cell.setNowrap(this.isNowrap());
        cell.setAdditionalParameters(this.additionalParameters);
        cell.setRowspan(this.getRowspan());
        return cell;
    }

    public boolean isExcel() {
        return false;
    }

    public void setStyle(Object style) {
        this.setStyle((HTMLStyle) style);
    }


}
