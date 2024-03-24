package com.msoft.mbi.cube.multi.generation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.*;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.MaskRenderer;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDynamicValueRenderer;

public class PrinterHTML implements Printer {

    private final Writer output;
    private FileOutputStream fos = null;
    private final List<String> classesIndex = new ArrayList<>();
    private final HashMap<String, String> classesCSS = new HashMap<>();
    @SuppressWarnings("unused")
    private String corBordasPadrao = "FFFFFF";
    private final Map<CellProperty, String> propriedadesEspecificasColuna;
    private final HtmlEffectApplier aplicadorEfeitoHTML;

    public PrinterHTML(String arquivoHTML) {
        try {
            fos = new FileOutputStream("c:\\" + arquivoHTML);
            this.output = new OutputStreamWriter(fos);
            this.propriedadesEspecificasColuna = new HashMap<>();
            this.aplicadorEfeitoHTML = new HtmlEffectApplierApply();
        } catch (FileNotFoundException e) {
            throw new CubeMathParserException("Não foi possível criar o arquivo.", e);
        }
    }

    public PrinterHTML(Writer output, boolean imprimeLinks) {
        this.output = output;
        this.propriedadesEspecificasColuna = new HashMap<>();
        if (imprimeLinks) {
            this.aplicadorEfeitoHTML = new HtmlEffectApplierApply();
        } else {
            this.aplicadorEfeitoHTML = new HtmlEffectApplierNotApply();
        }
    }

    private void imprime(String texto) {
        try {
            this.output.write(texto + "\n");
        } catch (IOException e) {
            throw new CubeMathParserException("Não foi possível realizar a impressão do valor.", e);
        }
    }

    public void printColumn(String cellProperty, String formattedValue) {
        this.imprime("<td class='" + cellProperty + "'>" + formattedValue + "</td>");
    }

    public void printColumn(String cellProperty, String formattedValue, int colspan, int rowspan) {
        this.imprime("<td colspan='" + colspan + "' rowspan='" + rowspan + "' class='" + cellProperty + "'>" + formattedValue + "</td>");
    }

    private void abreTabela() {
        this.imprime("<table class='mainTable'>");
    }

    private void fechaTabela() {
        this.imprime("</table>");
    }

    public String getNullValue() {
        return "-";
    }

    private void imprimeCassesCSS() {
        StringBuilder css = new StringBuilder();

        for (String cssName : this.classesIndex) {
            String cssValue = this.classesCSS.get(cssName);
            css.append(".").append(cssName).append(" {").append(cssValue).append("}\n");
        }

        this.imprime("\n<style type=\"text/css\">");
        this.imprime(css.toString());
        this.imprime("</style>\n");
    }

    public void endPrinting() {
        this.fechaTabela();
        try {
            if (this.fos != null) {
                output.close();
                fos.close();
            }
        } catch (IOException e) {
            throw new CubeMathParserException("Não foi possível finalizar a impressão da tabela.", e);
        }
    }

    private String createStyles(CellProperty cellProperty, String className) {
        StringBuilder description = new StringBuilder();

        description.append(cellProperty.getBackGroundColor() != null ? ("background-color: #" + cellProperty.getBackGroundColor() + "; ") : "")
                .append(cellProperty.getFontSize() != 0 ? ("font-size: " + cellProperty.getFontSize() + "px; ") : "")
                .append(cellProperty.getFontName() != null ? ("font-family: " + cellProperty.getFontName() + "; ") : "")
                .append(cellProperty.getFontColor() != null ? ("color: #" + cellProperty.getFontColor() + "; ") : "").append(cellProperty.isBold() ? "font-weight: bold; " : "")
                .append(cellProperty.isItalic() ? "font-style: italic; " : "")
                .append(cellProperty.getAlignment() != null ? ("text-align: " + cellProperty.getAlignment() + "; ") : "")
                .append(cellProperty.getSFloat() != null ? ("float: " + cellProperty.getSFloat() + "; ") : "")
                .append(cellProperty.getWidth() != 0 ? ("width: " + cellProperty.getWidth() + "px; ") : "")
                .append(cellProperty.isSpecificBorder() ? ("border: 1px solid #" + cellProperty.getBorderColor() + "; ") : "");

        for (String attribute : cellProperty.getExtraAttributes().keySet()) {
            description.append(attribute).append(": ").append(cellProperty.getExtraAttributes().get(attribute)).append("; ");
        }
        return description.toString();
    }

    public void addStyle(CellProperty cellProperty, String name) {
        String style = this.createStyles(cellProperty, name);

        this.classesIndex.add(name);
        this.classesCSS.put(name, style);
    }

    @Override
    public void openLine() {
        this.imprime("<tr>");
    }

    @Override
    public void openHeadLine() {
        this.imprime("<thead>");
    }

    @Override
    public void openBodyLine() {
        this.imprime("<tbody>");
    }

    @Override
    public void closeBodyLine() {
        this.imprime("</tbody>");
    }

    @Override
    public void closeHeadLine() {
        this.imprime("</thead>");
    }

    @Override
    public void closeLine() {
        this.imprime("</tr>");
    }

    private String applyHTMLEffect(Object valor, MaskRenderer effectDecorator) {
        return this.aplicadorEfeitoHTML.applyHtmlEffect(valor, effectDecorator);
    }

    private String applyDynamicHTMLEffect(Object printValue, String parameterValue, MaskLinkHTMLDynamicValueRenderer effectDecorator) {
        return this.aplicadorEfeitoHTML.applyDynamicHtmlEffect(printValue, parameterValue, effectDecorator);
    }

    private String getColumnHeaderContent(ColumnMetaData metaData, int colspan, int rowspan, String headerStyle) {
        StringBuilder sb = new StringBuilder();
        sb.append("	<th colspan='").append(colspan).append("' rowspan='").append(rowspan).append("' class='").append(headerStyle).append("'>");
        sb.append("		<div class='header'>");
        sb.append(this.getPrintValue(metaData));
        sb.append("		</div>");
        sb.append("	</th>");
        return sb.toString();
    }

    @Override
    public void printDimensionLineHeader(DimensionMetaData dimensionMetaData) {
        if (!dimensionMetaData.hasSequenceFields()) {
            this.printColumnHeader(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, dimensionMetaData);
        } else {
            this.printColumnHeader(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, dimensionMetaData, 2, 1);
        }
    }

    @Override
    public void printTotalPartialHeader(String cellProperty, String value, int colspan, int rowspan, DimensionMetaData dimensionMetaData) {
        this.printColumn(cellProperty, value, colspan, rowspan);
    }

    private String getDecimalPlacesValue(Double valor, int decimalPlaces) {
        NumberFormat numberInstance = NumberFormat.getInstance(Locale.GERMANY);
        numberInstance.setMinimumFractionDigits(decimalPlaces);
        numberInstance.setMaximumFractionDigits(decimalPlaces);
        return numberInstance.format(valor.doubleValue());
    }

    @Override
    public void printNumberValue(String cellProperty, Double valor, int decimalNumber) {
        this.printColumn(cellProperty, this.getDecimalPlacesValue(valor, decimalNumber));
    }

    public void printPercentNumberValue(String name, Double value, int decimalNumber) {
        String applyValue = this.getDecimalPlacesValue(value, decimalNumber) + "%";
        this.printColumn(name, applyValue);
    }

    @Override
    public void startPrinting() {
        this.imprimeCassesCSS();
        this.abreTabela();
    }

    @Override
    public void setDefaultBorderColor(String corBorda) {
        this.corBordasPadrao = corBorda;
    }

    public String getColumnSpecificStyle(ColumnMetaData metaData) {
        return this.propriedadesEspecificasColuna.get(metaData.getCellProperty());
    }

    private String getPrintValue(ColumnMetaData metaData) {
        return this.applyHTMLEffect(metaData.getTitle(), metaData.getHTMLEffectRenderer());
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData) {
        this.imprime(this.getColumnHeaderContent(metaData, 1, 1, cellProperty));
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData, int colspan, int rowspan) {
        this.imprime(this.getColumnHeaderContent(metaData, colspan, rowspan, cellProperty));
    }

    @Override
    public void printColumnHeader(String cellProperty, String title) {
        this.imprime("<th class='" + cellProperty + "'>" + title + "</th>");
    }

    @Override
    public void addColumnSpecificPropertyStyle(CellProperty cellProperty, String name) {
        this.propriedadesEspecificasColuna.put(cellProperty, name);
        this.addStyle(cellProperty, name);
    }

    @Override
    public String getEmptyValue() {
        return "&nbsp;";
    }

    @Override
    public void printColumnValue(String cellProperty, int colspan, int rowspan, Object valor, ColumnMetaData metaData) {
        String printValue = this.applyDynamicHTMLEffect(metaData.getFormattedValue(valor), (metaData.getFormattedValue(valor)), metaData.getHTMLDynamicEffectRenderer());
        this.printColumn(cellProperty + " " + this.getColumnSpecificStyle(metaData), printValue, colspan, rowspan);
    }

    @Override
    public void printColumnValue(String cellProperty, Object value, ColumnMetaData metaData) {
        String printValue = this.applyDynamicHTMLEffect(metaData.getFormattedValue(value), (value != null ? value.toString() : null), metaData.getHTMLDynamicEffectRenderer());
        this.printColumn(cellProperty + " " + this.getColumnSpecificStyle(metaData), printValue);
    }

    @Override
    public void printDimensionLineValue(String cellProperty, int colspan, int rowspan, Object valor, DimensionMetaData metaData) {
        this.printColumnValue(metaData.getDefaultStyle() + " " + cellProperty, colspan, rowspan, valor, metaData);
    }

    @Override
    public void printMetricValue(String cellProperty, Double valor, MetricMetaData metaData) {
        String metricProperty = this.getColumnSpecificStyle(metaData);
        if (metricProperty != null) {
            cellProperty += " " + metricProperty;
        }
        if (!metaData.isUsePercent())
            this.printNumberValue(cellProperty, valor, metaData.getDecimalPlacesNumber());
        else
            this.printPercentNumberValue(cellProperty, valor, metaData.getDecimalPlacesNumber());
    }

    @Override
    public void printSequenceField(DimensionMetaData dimensionMetaData, String sequence, int colspan, int rowspan) {
        this.printColumn(CellProperty.CELL_PROPERTY_SEQUENCE, sequence, colspan, rowspan);
    }

    @Override
    public void printSequenceField(String sequence) {
        this.printColumn(CellProperty.CELL_PROPERTY_SEQUENCE, sequence);
    }

    @Override
    public void addLinkStyle(CellProperty cellProperty, String name) {
        this.addStyle(cellProperty, name);
    }

    @Override
    public void addColumnHeaderStyle(CellProperty cellProperty, String name) {
        CellProperty cellPropertyCabecalho = new CellProperty();
        cellPropertyCabecalho.setBorderColor(cellProperty.getBorderColor());
        cellPropertyCabecalho.setSpecificBorder(cellProperty.isSpecificBorder());
        cellPropertyCabecalho.setFontColor(cellProperty.getFontColor());
        cellPropertyCabecalho.setFontName(cellProperty.getFontName());
        cellPropertyCabecalho.setBold(cellProperty.isBold());
        cellPropertyCabecalho.setFontSize(cellProperty.getFontSize());
        cellPropertyCabecalho.setBackGroundColor(cellProperty.getBackGroundColor());
        for (String atributo : cellProperty.getExtraAttributes().keySet()) {
            cellPropertyCabecalho.addExtraAttributes(atributo, cellProperty.getExtraAttributes().get(atributo));
        }
        this.addStyle(cellPropertyCabecalho, name);

    }
}
