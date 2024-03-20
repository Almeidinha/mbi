package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.util.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HTMLTable extends HTMLTableComponent {

    private int borderWidth;
    private String backGroundImage = "";
    private int callPadding;
    private int cellSpacing;
    private int cols;
    private String rules = "";
    private HTMLLine currentLine;
    private int currentLineIndex = -1;
    private List<HTMLLine> lines;
    private List<String> new_lines_index = new ArrayList<>();

    public HTMLTable() {
        this.lines = new ArrayList<>();
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

    public List<String> getNewLines() {
        return new_lines_index;
    }

    public void addNewLine(int index) {
        this.new_lines_index.add(String.valueOf(index));
    }

    public void setNewLines(List<String> new_lines) {
        this.new_lines_index = new_lines;
    }

    public boolean isNewLine(int index) {
        if (new_lines_index != null) {
            for (String newLineIndex : new_lines_index) {
                if (Integer.parseInt(newLineIndex) == index) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLine(HTMLLine line) {
        this.lines.add(line);
        this.currentLineIndex++;
        this.currentLine = line;
    }

    public void removeLine(HTMLLine line) {
        this.lines.remove(line);
    }

    public int getLineIndex(HTMLLine line) {
        if (this.currentLine.equals(line)) {
            return this.currentLineIndex;
        }
        return this.lines.indexOf(line);
    }

    public HTMLLine getNextLine(HTMLLine line) {
        int index = this.lines.indexOf(line);
        if (index >= 0 && index + 1 < this.lines.size()) {
            return this.lines.get(index + 1);
        }
        return null;
    }

    public HTMLLine getPreviousLine(HTMLLine line) {
        int index = this.lines.indexOf(line);
        if (index > 0) {
            return this.lines.get(index - 1);
        }
        return null;
    }

    public void buildComponent(Writer out) throws BIIOException {
        try {
            out.write("<table ");
            out.write(this.getProperties());

            writeAttributeIfNotEmpty(out, "background", this.backGroundImage);
            writeAttributeIfNotEmpty(out, "cols", String.valueOf(this.cols));
            writeAttributeIfNotEmpty(out, "rules", this.rules);
            out.write(">\n");

            for (HTMLLine line : this.lines) {
                line.buildComponent(out);
            }

            out.write("</table>\n");
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

    public Object clone() throws CloneNotSupportedException {
        HTMLTable table = (HTMLTable)super.clone();
        table.setAlignment(this.alignment);
        table.setVerticalAlignment(this.verticalAlignment);
        table.setHeight(this.height);
        table.setCellClass(this.cellClass);
        table.setCols(this.cols);
        table.setContent(this.content);
        table.setBorderColor(this.borderColor);
        table.setLightBorderColor(this.lightBorderColor);
        table.setDarkBorderColor(this.darkBorderColor);
        table.setBackGroundColor(this.backGroundColor);
        table.setEditable(this.editable);
        table.setStyle(this.style);
        table.setId(this.id);
        table.setBackGroundImage(this.backGroundImage);
        table.setWidth(this.width);
        table.setNewLines(this.new_lines_index);
        table.setAdditionalParameters(this.additionalParameters);
        table.setRules(this.rules);
        table.setLines(this.lines);
        return table;
    }
}
