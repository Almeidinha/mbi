package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metaData.HTMLLineMask;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnSVG;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLDynamicText;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.BIDriller;
import com.msoft.mbi.data.api.data.indicator.Indicator;

import java.io.Writer;

public class HtmlHelper {

    public static HTMLTable buildStringDrillUp(boolean isDrillUp) {
        HTMLTable tab_drillup = new HTMLTable();
        tab_drillup.setId("opcao_drill_up");
        tab_drillup.addLine(new HTMLLine());
        tab_drillup.getCurrentLine().addCell(new HTMLCell());
        LinkHTMLColumnSVG image;
        if (isDrillUp) {
            image = new LinkHTMLColumnSVG("vect-arrow-square-up-left", "Drill Up", 18, 18);
            image.addParameter("onclick", "addDrillUp(0)");
        } else {
            image = new LinkHTMLColumnSVG();
        }
        tab_drillup.getCurrentLine().getCurrentCell().setContent(image);
        return tab_drillup;
    }

    public static String buildDefaultStringDrillUp(Indicator indicator) {
        StringBuilder drillUpTable = new StringBuilder();

        drillUpTable.append("<table>");
        BIDriller bi = new BIDriller();
        Field field = bi.searchCurrentField(indicator);
        Field tempField = null;
        if (field != null) {
            tempField = bi.getNextDrillDownSequence(field, indicator.getFields());
        }

        drillUpTable.append("<tr editable ='no'>")
                .append("<th style='display:flex; text-align:left' colspan = '2' >");

        if (tempField == null) {
            drillUpTable.append("<div onclick='addDrillUp(").append(indicator.getCode())
                    .append(")' class='vect-arrow-square-up-left' style='width: 18px; height: 18px; margin: auto;' title='Drill-Up'></div>");
        } else {
            BIDriller di = new BIDriller();
            Field aux = di.searchCurrentField(indicator);
            drillUpTable.append("<div onclick='addDrillUp(").append(indicator.getCode())
                    .append(")' class='vect-arrow-square-up-left' style='width: 18px; height: 18px; margin: auto;' title='Drill-Up'></div>");
            drillUpTable.append("<div onclick='DrillDownSemFiltro(").append(aux.getFieldId()).append(",").append(indicator.getCode())
                    .append(")' class='vect-arrow-square-down-right' style='width: 18px; height: 18px; margin: auto;' title='Descer um nível sem utilizar Drill-Down'></div>");
        }
        drillUpTable.append("</th>")
                .append("</tr>")
                .append("</table>");

        return drillUpTable.toString();
    }

    public static void buildStringTitleAndDrillUp(Writer out, boolean noLinks, Object drillUpTable, String name) throws BIException {
        HTMLTable titleTab = buildStringTitle(name);
        HTMLTable table = new HTMLTable();
        table.addLine(new HTMLLine());
        table.getCurrentLine().addCell(new HTMLCell());
        table.getCurrentLine().getCurrentCell().setContent(titleTab);
        table.addLine(new HTMLLine());
        table.getCurrentLine().addCell(new HTMLCell());
        table.setWidth("100%");
        table.getCurrentLine().getCurrentCell().setContent(drillUpTable);
        table.toString(out);
    }

    public static HTMLTable buildStringTitle(String name) {
        HTMLTable titleTab = new HTMLTable();
        titleTab.setId("nome_indicador");
        titleTab.setWidth("100");
        titleTab.addLine(new HTMLLine());
        titleTab.getCurrentLine().setEditable("yes");
        titleTab.getCurrentLine().addCell(new HTMLCell());
        titleTab.getCurrentLine().getCurrentCell().setAlignment("center");
        titleTab.getCurrentLine().getCurrentCell().setId("nome_titulo");
        titleTab.getCurrentLine().getCurrentCell().setNowrap(true);

        HTMLStyle titleStile = HTMLStyle
                .builder()
                .backgroundColor("#FFFFFF")
                .fontSize(14)
                .fontFamily("verdana")
                .fontColor("#000080")
                .fontWeight("bold")
                .fontStyle("normal")
                .textDecoration("none")
                .additionalParameters("cursor: pointer;")
                .build();

        titleTab.getCurrentLine().getCurrentCell().setStyle(titleStile);
        titleTab.getCurrentLine().getCurrentCell().setEditable("yes");
        titleTab.getCurrentLine().getCurrentCell().setContent(name);
        return titleTab;
    }

    public static void createDimensionLineMask(Field dimensionField, MetaDataField dimensionCube, Indicator indicator) {
        if (dimensionField == null || dimensionCube == null) {
            return;
        }

        if (dimensionField.isNavigable()) {
            addSVGLinkToDimensionCube(dimensionCube, dimensionField, "mais", "btMais vect-plus-sign", "Drill Down", 14, 14);
        } else if (dimensionField.isNavigableUpwards()) {
            addSVGLinkToDimensionCube(dimensionCube, dimensionField, "menos", "btMenos vect-minus-sign", "Drill Up", 14, 14);
        }

        String image = dimensionField.getNickname() + dimensionField.getFieldId();
        String svgClass = (indicator.getFilters().getDimensionFilter() != null && indicator.checkFilters(indicator.getFilters().getDimensionFilter(), dimensionField)) ?
                "btFiltro vect-filter-yellow" : "btFiltro vect-filter-silver";
        addSVGLinkToDimensionCube(dimensionCube, dimensionField, image, svgClass, "Filtrar Dimensão", 18, 18);

        if (dimensionField.isNavigableUpwards()) {
            createHTMLDrillDownMask(dimensionField, dimensionCube, indicator.getCode());
        }
    }

    public static void createHTMLDrillDownMask(Field dimensionField, MetaDataField dimensionCube, Integer indicatorCode) {
        if (dimensionField == null || dimensionCube == null) {
            return;
        }

        LinkHTMLDynamicText htmlDynamicText = new LinkHTMLDynamicText("data-dimension-value");
        htmlDynamicText.addParameter("data-code-col", String.valueOf(dimensionField.getFieldId()));
        htmlDynamicText.addParameter("data-code-indicator", String.valueOf(indicatorCode));

        HTMLLineMask mascaraLinkHTML = new HTMLLineMask("drilldownFiltro", HTMLLineMask.DYNAMIC_TYPE, htmlDynamicText);
        dimensionCube.addHTMLLineMask(mascaraLinkHTML);
    }

    private static void addSVGLinkToDimensionCube(MetaDataField dimensionCube, Field dimensionField, String id, String svgClass, String svgTitle, int width, int height) {
        LinkHTMLColumnSVG svgHTML = new LinkHTMLColumnSVG(id, svgClass, svgTitle, height, width);
        svgHTML.addParameter("data-code-col", String.valueOf(dimensionField.getFieldId()));
        HTMLLineMask mascaraLinkHTML = new HTMLLineMask("drilldown", HTMLLineMask.VALUE_TYPE_AFTER, svgHTML);
        dimensionCube.addHTMLLineMask(mascaraLinkHTML);
    }

    public static void configureSorting(Field field, MetaDataField campo) {
        LinkHTMLColumnSVG svgHTML = new LinkHTMLColumnSVG("desc", "btOrdena vect-sort-down", "Ordenar de forma decrescente", 14, 14);
        // configure svgHTML
        campo.addHTMLLineMask(new HTMLLineMask("ordenacao", HTMLLineMask.VALUE_TYPE_AFTER, svgHTML));

        svgHTML = new LinkHTMLColumnSVG("asc", "btOrdena vect-sort-up", "Ordenar de forma crescente", 14, 14);
        // configure svgHTML
        campo.addHTMLLineMask(new HTMLLineMask("ordenacao", HTMLLineMask.VALUE_TYPE_AFTER, svgHTML));
    }

}
