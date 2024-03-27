package com.msoft.mbi.data.api.data.htmlbuilder;
import com.msoft.mbi.data.api.data.exception.BIIOException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@Getter
public class HTMLLine extends HTMLTableComponent implements Line {

    @Setter
    private List<Cell> cells;
    private HTMLCell currentCell;

    @Setter
    private boolean appliedAlert = false;

    public HTMLLine() {
        this.cells = new ArrayList<>();
    }

    public void addCell(HTMLCell cell) {
        this.cells.add(cell);
        this.currentCell = cell;
    }

    public HTMLCell getPosteriorCell(HTMLCell cell) {
        int index = this.cells.indexOf(cell);
        if (index >= 0 && index + 1 < this.cells.size()) {
            return (HTMLCell)this.cells.get(index + 1);
        }
        return null;
    }

    public HTMLCell getPreviousCell(HTMLCell cell) {
        int index = this.cells.indexOf(cell);
        if (index > 0) {
            return (HTMLCell)this.cells.get(index - 1);
        }
        return null;
    }

    public void buildComponent(Writer out) throws BIIOException {
        try {
            out.write("<tr ");
            out.write(this.getProperties());
            out.write(">\n");

            for (Cell cell : this.cells) {
                ((HTMLCell) cell).toString(out);
            }

            out.write("</tr>\n");
        } catch (IOException e) {
            BIIOException bi = new BIIOException("Erro ao montar célula da tabela.", e);
            bi.setErrorCode(BIIOException.BUFFER_WRITE_ERROR);
            bi.setLocal(this.getClass(), "montaComponente(Writer)");
            throw bi;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        HTMLLine line = (HTMLLine) super.clone();
        line.setAlignment(this.getAlignment());
        line.setVerticalAlignment(this.getVerticalAlignment());
        line.setCellClass(this.getCellClass());
        line.setBorderColor(this.getBorderColor());
        line.setLightBorderColor(this.getLightBorderColor());
        line.setDarkBorderColor(this.getDarkBorderColor());
        line.setBackGroundColor(this.getBackGroundColor());
        line.setEditable(this.getEditable());
        line.setStyle(this.getStyle());
        line.setId(this.getId());
        line.setAdditionalParameters(this.additionalParameters);
        line.setHeight(this.height);
        line.setWidth(this.width);
        line.setCells(this.cells);
        return line;
    }

    public HTMLCell getFirstCell() {
        return this.cells.isEmpty() ? null : (HTMLCell) this.cells.get(0);
    }

    public int getLargestRowSpan() {
        return this.cells.stream()
                .map(cell -> (HTMLCell) cell)
                .mapToInt(HTMLCell::getRowspan)
                .max()
                .orElse(0);
    }

    public void setStyle(Object style) {
        this.setStyle((HTMLStyle) style);
    }

    public void setStyle(Object style, boolean isDimension) {
        this.setStyle(style);
    }


    public Object getAppliedStyle() {
        return null;
    }

    public void setAppliedStyle(Object style) {
    }

    public void resetCellStyles() {
        this.cells.stream()
                .map(cell -> (HTMLCell) cell)
                .filter(cell -> cell != null && !cell.isAppliedAlert() && !cell.isDimensionColumn())
                .forEach(cell -> cell.setStyle(null));
    }
}
