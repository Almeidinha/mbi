package com.msoft.mbi.cube.multi.generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLValorDinamicoRenderer;
import lombok.Getter;
import lombok.Setter;


import java.text.NumberFormat;
import java.util.*;

@Getter
@Setter
public class JsonPrinter implements Printer {

    private final ObjectMapper mapper;

    private final ObjectNode styles;

    private final ArrayNode headers;

    private final ArrayNode rows;

    ObjectNode resultNode;

    private final Map<CellProperty, String> columnSpecificProperties;

    private final AplicadorEfeitoHTML aplicadorEfeitoHTML;

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String FONT_SIZE = "fontSize";
    private static final String FONT_FAMILY = "fontFamily";
    private static final String COLOR = "color";
    private static final String FONT_WEIGHT = "fontWeight";
    private static final String TEXT_ALIGN = "textAlign";
    private static final String FLOAT = "float";
    private static final String WIDTH = "width";
    private static final String BORDER_COLOR = "borderColor";

    public JsonPrinter() {
        this.mapper = new ObjectMapper();
        this.rows = mapper.createArrayNode();
        this.headers = mapper.createArrayNode();
        this.styles = mapper.createObjectNode();
        this.resultNode = mapper.createObjectNode();
        this.columnSpecificProperties = new HashMap<>();
        this.aplicadorEfeitoHTML = new AplicadorEfeitoHTMLAplica();
    }

    public JsonPrinter(ObjectNode jsonNode) {
        this();
        this.resultNode = jsonNode;
    }

    private void print(String text) {
        System.out.println("texto : " + text);
    }

    @Override
    public void startPrinting() {

    }

    @Override
    public void endPrinting() {
        this.resultNode.set("headers", this.headers);
        this.resultNode.set("rows", this.rows);
        this.resultNode.set("styles", this.styles);
    }

    @Override
    public String getNullValue() {
        return null;
    }

    @Override
    public String getEmptyValue() {
        return null;
    }

    @Override
    public void setDefaultBorderColor(String corBorda) {

    }

    @Override
    public void openLine() {

    }

    @Override
    public void closeLine() {

    }

    @Override
    public void addStyle(CellProperty cellProperty, String name) {
        this.createStyles(cellProperty, name);

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
        for (String attribute : cellProperty.getExtraAttributes().keySet()) {
            cellPropertyCabecalho.addExtraAttributes(attribute, cellProperty.getExtraAttributes().get(attribute));
        }
        this.addStyle(cellPropertyCabecalho, name);
    }

    @Override
    public void addColumnSpecificPropertyStyle(CellProperty cellProperty, String name) {
        this.columnSpecificProperties.put(cellProperty, name);
        this.addStyle(cellProperty, name);
    }

    @Override
    public void addLinkStyle(CellProperty cellProperty, String name) {
        this.addStyle(cellProperty, name);
    }

    @Override
    public void printColumn(String cellProperty, String formattedValue) {
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.put("className", cellProperty);
        jsonObject.put("value", formattedValue);
        this.rows.add(jsonObject);
    }

    @Override
    public void printColumn(String cellProperty, String formattedValue, int colspan, int rowspan) {
        ObjectNode jsonObject = this.mapper.createObjectNode();
        jsonObject.put("colspan", colspan);
        jsonObject.put("rowspan", rowspan);
        jsonObject.put("className", cellProperty);
        jsonObject.put("value", formattedValue);
        this.rows.add(jsonObject);
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData) {
        ObjectNode jsonObject = this.mapper.createObjectNode();
        jsonObject.put("title", metaData.getTitle());
        jsonObject.set("properties", this.getHeaderColumnContent(metaData, 1, 1, cellProperty));

        headers.add(jsonObject);
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData, int colspan, int rowspan) {
        ObjectNode jsonObject = this.mapper.createObjectNode();
        jsonObject.put("title", metaData.getTitle());
        jsonObject.set("properties", this.getHeaderColumnContent(metaData, colspan, rowspan, cellProperty));
        this.headers.add(jsonObject);
    }

    @Override
    public void printColumnHeader(String cellProperty, String title) {
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.put("title", title);
        jsonObject.put("property", cellProperty);
        this.headers.add(jsonObject);
    }

    @Override
    public void printDimensionLineHeader(DimensaoMetaData dimensaoMetaData) {
        if (!dimensaoMetaData.hasSequenceFields()) {
            this.printColumnHeader(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, dimensaoMetaData);
        } else {
            this.printColumnHeader(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, dimensaoMetaData, 2, 1);
        }
    }

    @Override
    public void printTotalPartialHeader(String cellProperty, String value, int colspan, int rowspan, DimensaoMetaData dimensaoMetaData) {
        this.printColumn(cellProperty, value, colspan, rowspan);
    }

    @Override
    public void printColumnValue(String cellProperty, int colspan, int rowspan, Object value, ColumnMetaData metaData) {
        String printValue = this.applyDynamicHtmlEffect(metaData.getFormattedValue(value), (metaData.getFormattedValue(value)), metaData.getHTMLDynamicEffectRenderer());
        this.printColumn(cellProperty + " " + this.getColumnSpecificStyle(metaData), printValue, colspan, rowspan);
    }

    @Override
    public void printColumnValue(String cellProperty, Object value, ColumnMetaData metaData) {
        String printValue = this.applyDynamicHtmlEffect(metaData.getFormattedValue(value), (value != null ? value.toString() : null), metaData.getHTMLDynamicEffectRenderer());
        this.printColumn(cellProperty + " " + this.getColumnSpecificStyle(metaData), printValue);
    }


    @Override
    public void printDimensionLineValue(String cellProperty, int colspan, int rowspan, Object valor, DimensaoMetaData metaData) {
        this.printColumnValue(metaData.getEstiloPadrao() + " " + cellProperty, colspan, rowspan, valor, metaData);
    }

    @Override
    public void printMetricValue(String cellProperty, Double valor, MetricMetaData metaData) {
        String metricCellProperty = this.getColumnSpecificStyle(metaData);
        if (metricCellProperty != null) {
            cellProperty += " " + metricCellProperty;
        }
        if (!metaData.isUsePercent())
            this.printNumberValue(cellProperty, valor, metaData.getDecimalPlacesNumber());
        else
            this.printPercentNumberValue(cellProperty, valor, metaData.getDecimalPlacesNumber());
    }

    @Override
    public void printNumberValue(String cellProperty, Double valor, int decimalNumber) {
        this.printColumn(cellProperty, this.getDecimalPlacesValue(valor, decimalNumber));
    }

    @Override
    public void printSequenceField(DimensaoMetaData dimensaoMetaData, String sequence, int colspan, int rowspan) {
        this.printColumn(CellProperty.CELL_PROPERTY_SEQUENCE, sequence, colspan, rowspan);
    }

    @Override
    public void printSequenceField(String sequence) {
        this.printColumn(CellProperty.CELL_PROPERTY_SEQUENCE, sequence);
    }

    @Override
    public void printPercentNumberValue(String name, Double value, int decimalNumber) {
        String valueToApply = this.getDecimalPlacesValue(value, decimalNumber) + "%";
        this.printColumn(name, valueToApply);
    }

    @Override
    public void openHeadLine() {

    }

    @Override
    public void closeHeadLine() {

    }

    @Override
    public void openBodyLine() {

    }

    @Override
    public void closeBodyLine() {

    }

    private String getDecimalPlacesValue(Double valor, int decimalNumber) {
        NumberFormat numberInstance = NumberFormat.getInstance(Locale.GERMANY);
        numberInstance.setMinimumFractionDigits(decimalNumber);
        numberInstance.setMaximumFractionDigits(decimalNumber);
        return numberInstance.format(valor.doubleValue());
    }

    public String getColumnSpecificStyle(ColumnMetaData metaData) {
        return this.columnSpecificProperties.get(metaData.getCellProperty());
    }

    private void addToPropertyTOJsonObject(ObjectNode jsonObject, String propName, Object propValue) {
        this.addToPropertyTOJsonObject(jsonObject, propName, propValue, "", "");
    }

    private void addToPropertyTOJsonObject(ObjectNode jsonObject, String propName, Object propValue, String prefix, String suffix) {
        if (propValue instanceof Integer) {
            if (!propValue.equals(0)) {
                jsonObject.put(propName, prefix + propValue + suffix);
            }
        } else if (propValue instanceof String) {
            Optional.of(propValue).ifPresent(color -> jsonObject.put(propName,  prefix + propValue + suffix));
        }
    }

    private void createStyles(CellProperty cellProperty, String name) {
        ObjectNode jsonObject = this.mapper.createObjectNode();

        addToPropertyTOJsonObject(jsonObject, BACKGROUND_COLOR, cellProperty.getBackGroundColor(), "#", "");
        addToPropertyTOJsonObject(jsonObject, FONT_SIZE, cellProperty.getFontSize(), "", "px");
        addToPropertyTOJsonObject(jsonObject, FONT_FAMILY, cellProperty.getFontName());
        addToPropertyTOJsonObject(jsonObject, COLOR, cellProperty.getFontColor(), "#", "");
        addToPropertyTOJsonObject(jsonObject, FONT_WEIGHT, cellProperty.isBold() ? "bold" : null);
        addToPropertyTOJsonObject(jsonObject, TEXT_ALIGN, cellProperty.getAlignment());
        addToPropertyTOJsonObject(jsonObject, FLOAT, cellProperty.getSFloat());
        addToPropertyTOJsonObject(jsonObject, WIDTH, cellProperty.getWidth(), "", "px");
        addToPropertyTOJsonObject(jsonObject, BORDER_COLOR, cellProperty.getBorderColor(), "#", "");

        Optional.ofNullable(cellProperty.getExtraAttributes()).ifPresent(extraAttributes -> {
            for (String attribute : extraAttributes.keySet()) {
                jsonObject.put(attribute, cellProperty.getExtraAttributes().get(attribute));
            }
        });

        this.styles.set(name, jsonObject);

    }

    private ObjectNode getHeaderColumnContent(ColumnMetaData metaData, int colspan, int rowspan, String headerStyle) {
        ObjectNode jsonObject = this.mapper.createObjectNode();

        jsonObject.put("colspan", colspan);
        jsonObject.put("rowspan", rowspan);
        jsonObject.put("className", headerStyle);
        jsonObject.put("html", this.getPrintValue(metaData));

        return jsonObject;
    }

    private String getPrintValue(ColumnMetaData metaData) {
        return this.applyHtmlEffect(metaData.getTitle(), metaData.getHTMLEffectRenderer());
    }

    private String applyHtmlEffect(Object value, MascaraRenderer htmlDecoratorEffect) {
        return this.aplicadorEfeitoHTML.aplicaEfeitoHTML(value, htmlDecoratorEffect);
    }

    private String applyDynamicHtmlEffect(Object printValue, String parameterValue, MascaraLinkHTMLValorDinamicoRenderer htmlDecoratorEffect) {
        return this.aplicadorEfeitoHTML.aplicaEfeitoHTMLDinamico(printValue, parameterValue, htmlDecoratorEffect);
    }
}
