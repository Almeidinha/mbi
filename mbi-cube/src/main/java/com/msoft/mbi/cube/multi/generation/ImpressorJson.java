package com.msoft.mbi.cube.multi.generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLValorDinamicoRenderer;
import lombok.Getter;
import lombok.Setter;


import java.text.NumberFormat;
import java.util.*;

@Getter
@Setter
public class ImpressorJson implements Impressor {

    private final ObjectMapper mapper;

    private final ObjectNode styles;

    private final ArrayNode headers;

    private final ArrayNode rows;

    ObjectNode resultNode;

    private final Map<CellProperty, String> propriedadesEspecificasColuna;

    private final AplicadorEfeitoHTML aplicadorEfeitoHTML;

    private static final String BACKGROUND_COLOR = "background-color";
    private static final String FONT_SIZE = "font-size";
    private static final String FONT_FAMILY = "font-family";
    private static final String COLOR = "color";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String TEXT_ALIGN = "text-align";
    private static final String FLOAT = "float";
    private static final String WIDTH = "width";
    private static final String BORDER_COLOR = "border-color";

    public ImpressorJson() {
        this.mapper = new ObjectMapper();
        this.rows = mapper.createArrayNode();
        this.headers = mapper.createArrayNode();
        this.styles = mapper.createObjectNode();
        this.resultNode = mapper.createObjectNode();
        this.propriedadesEspecificasColuna = new HashMap<>();
        this.aplicadorEfeitoHTML = new AplicadorEfeitoHTMLAplica();
    }

    public ImpressorJson(ObjectNode jsonNode) {
        this();
        this.resultNode = jsonNode;
    }

    private void imprime(String texto) {
        System.out.println("texto : " + texto);
    }

    @Override
    public void iniciaImpressao() {

    }

    @Override
    public void finalizaImpressao() {
        this.resultNode.set("headers", this.headers);
        this.resultNode.set("rows", this.rows);
        this.resultNode.set("styles", this.styles);
    }

    @Override
    public String getValorNulo() {
        return null;
    }

    @Override
    public String getValorVazio() {
        return null;
    }

    @Override
    public void setCorBordasPadrao(String corBorda) {

    }

    @Override
    public void abreLinha() {

    }

    @Override
    public void fechaLinha() {

    }

    @Override
    public void adicionaEstilo(CellProperty cellProperty, String name) {
        this.createStyles(cellProperty, name);

    }

    @Override
    public void adicionaEstiloCabecalhoColuna(CellProperty cellProperty, String name) {
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
        this.adicionaEstilo(cellPropertyCabecalho, name);
    }

    @Override
    public void adicionaEstiloPropriedadeEspecificaColuna(CellProperty propriedadeMetrica, String nomeEstilo) {
        this.propriedadesEspecificasColuna.put(propriedadeMetrica, nomeEstilo);
        this.adicionaEstilo(propriedadeMetrica, nomeEstilo);
    }

    @Override
    public void adicionaEstiloLink(CellProperty cellProperty, String name) {
        this.adicionaEstilo(cellProperty, name);
    }

    @Override
    public void imprimeColuna(String property, String formattedValue) {
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.put("className", property);
        jsonObject.put("value", formattedValue);
        this.rows.add(jsonObject);
    }

    @Override
    public void imprimeColuna(String property, String formattedValue, int colspan, int rowspan) {
        ObjectNode jsonObject = this.mapper.createObjectNode();
        jsonObject.put("colspan", colspan);
        jsonObject.put("rowspan", rowspan);
        jsonObject.put("className", property);
        jsonObject.put("value", formattedValue);
        this.rows.add(jsonObject);
    }

    @Override
    public void imprimeCabecalhoColuna(String property, ColunaMetaData metaData) {
        ObjectNode jsonObject = this.mapper.createObjectNode();
        jsonObject.put("title", metaData.getTitulo());
        jsonObject.set("properties", this.getHeaderColumnContent(metaData, 1, 1, property));

        headers.add(jsonObject);
    }

    @Override
    public void imprimeCabecalhoColuna(String property, ColunaMetaData metaData, int colspan, int rowspan) {
        ObjectNode jsonObject = this.mapper.createObjectNode();
        jsonObject.put("title", metaData.getTitulo());
        jsonObject.set("properties", this.getHeaderColumnContent(metaData, colspan, rowspan, property));
        this.headers.add(jsonObject);
    }

    @Override
    public void imprimeCabecalhoColuna(String property, String title) {
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.put("title", title);
        jsonObject.put("property", property);
        this.headers.add(jsonObject);
    }

    @Override
    public void imprimeCabecalhoDimensaoLinha(DimensaoMetaData dimensaoMetaData) {
        if (!dimensaoMetaData.hasCampoSequencia()) {
            this.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_DIMENSAO, dimensaoMetaData);
        } else {
            this.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_DIMENSAO, dimensaoMetaData, 2, 1);
        }
    }

    @Override
    public void imprimeCabecalhoTotalParcial(String property, String valor, int colspan, int rowspan, DimensaoMetaData dimensaoTotalizada) {
        this.imprimeColuna(property, valor, colspan, rowspan);
    }

    @Override
    public void imprimeValorColuna(String cellProperty, int colspan, int rowspan, Object value, ColunaMetaData metaData) {
        String printValue = this.applyDynamicHtmlEffect(metaData.getFormattedValue(value), (metaData.getFormattedValue(value)), metaData.getEfeitosHTMLValorDecorator());
        this.imprimeColuna(cellProperty + " " + this.getColumnSpecificStyle(metaData), printValue, colspan, rowspan);
    }

    @Override
    public void imprimeValorColuna(String cellProperty, Object value, ColunaMetaData metaData) {
        String printValue = this.applyDynamicHtmlEffect(metaData.getFormattedValue(value), (value != null ? value.toString() : null), metaData.getEfeitosHTMLValorDecorator());
        this.imprimeColuna(cellProperty + " " + this.getColumnSpecificStyle(metaData), printValue);
    }


    @Override
    public void imprimeValorDimensaoLinha(String property, int colspan, int rowspan, Object valor, DimensaoMetaData metaData) {
        this.imprimeValorColuna(metaData.getEstiloPadrao() + " " + property, colspan, rowspan, valor, metaData);
    }

    @Override
    public void imprimeValorMetrica(String property, Double valor, MetricaMetaData metaData) {
        String propriedadeMetrica = this.getColumnSpecificStyle(metaData);
        if (propriedadeMetrica != null) {
            property += " " + propriedadeMetrica;
        }
        if (!metaData.isUtilizaPercentual())
            this.imprimeValorNumero(property, valor, metaData.getNCasasDecimais());
        else
            this.imprimeValorNumeroPercentual(property, valor, metaData.getNCasasDecimais());
    }

    @Override
    public void imprimeValorNumero(String property, Double valor, int decimalNumber) {
        this.imprimeColuna(property, this.getDecimalPlacesValue(valor, decimalNumber));
    }

    @Override
    public void imprimeCampoSequencia(DimensaoMetaData dimensaoMetaData, String sequence, int colspan, int rowspan) {
        this.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_SEQUENCIA, sequence, colspan, rowspan);
    }

    @Override
    public void imprimeCampoSequencia(String sequence) {
        this.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_SEQUENCIA, sequence);
    }

    @Override
    public void imprimeValorNumeroPercentual(String property, Double valor, int decimalNumber) {
        String valueToApply = this.getDecimalPlacesValue(valor, decimalNumber) + "%";
        this.imprimeColuna(property, valueToApply);
    }

    @Override
    public void abreLinhaHead() {

    }

    @Override
    public void fechaLinhaHead() {

    }

    @Override
    public void abreLinhaBody() {

    }

    @Override
    public void fechaLinhaBody() {

    }

    private String getDecimalPlacesValue(Double valor, int decimalNumber) {
        NumberFormat numberInstance = NumberFormat.getInstance(Locale.GERMANY);
        numberInstance.setMinimumFractionDigits(decimalNumber);
        numberInstance.setMaximumFractionDigits(decimalNumber);
        return numberInstance.format(valor.doubleValue());
    }

    public String getColumnSpecificStyle(ColunaMetaData metaData) {
        return this.propriedadesEspecificasColuna.get(metaData.getCellProperty());
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

    private ObjectNode getHeaderColumnContent(ColunaMetaData metaData, int colspan, int rowspan, String headerStyle) {
        ObjectNode jsonObject = this.mapper.createObjectNode();

        jsonObject.put("colspan", colspan);
        jsonObject.put("rowspan", rowspan);
        jsonObject.put("className", headerStyle);
        jsonObject.put("html", this.getPrintValue(metaData));

        return jsonObject;
    }

    private String getPrintValue(ColunaMetaData metaData) {
        return this.applyHtmlEffect(metaData.getTitulo(), metaData.getEfeitoHTMLDecorator());
    }

    private String applyHtmlEffect(Object value, MascaraRenderer htmlDecoratorEffect) {
        return this.aplicadorEfeitoHTML.aplicaEfeitoHTML(value, htmlDecoratorEffect);
    }

    private String applyDynamicHtmlEffect(Object printValue, String parameterValue, MascaraLinkHTMLValorDinamicoRenderer htmlDecoratorEffect) {
        return this.aplicadorEfeitoHTML.aplicaEfeitoHTMLDinamico(printValue, parameterValue, htmlDecoratorEffect);
    }
}
