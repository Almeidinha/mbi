package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metaData.HTMLLineMask;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLSVGColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoDinamico;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.BIDriller;
import com.msoft.mbi.data.api.data.indicator.Indicator;

import java.io.Writer;

public class HtmlHelper {

    public static TabelaHTML buildStringDrillUp(boolean isDrillUp) {
        TabelaHTML tab_drillup = new TabelaHTML();
        tab_drillup.setId("opcao_drill_up");
        tab_drillup.addLinha(new LinhaHTML());
        tab_drillup.getLinhaAtual().addCelula(new CelulaHTML());
        LinkHTMLSVGColuna imagem;
        if (isDrillUp) {
            imagem = new LinkHTMLSVGColuna("vect-arrow-square-up-left", "Drill Up", 18, 18);
            imagem.addParametro("onclick", "addDrillUp(0)");
        } else {
            imagem = new LinkHTMLSVGColuna();
        }
        tab_drillup.getLinhaAtual().getCelulaAtual().setConteudo(imagem);
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
        TabelaHTML titleTab = buildStringTitle(name);
        TabelaHTML table = new TabelaHTML();
        table.addLinha(new LinhaHTML());
        table.getLinhaAtual().addCelula(new CelulaHTML());
        table.getLinhaAtual().getCelulaAtual().setConteudo(titleTab);
        table.addLinha(new LinhaHTML());
        table.getLinhaAtual().addCelula(new CelulaHTML());
        table.setLargura("100%");
        table.getLinhaAtual().getCelulaAtual().setConteudo(drillUpTable);
        table.toString(out);
    }

    public static TabelaHTML buildStringTitle(String name) {
        TabelaHTML titleTab = new TabelaHTML();
        titleTab.setId("nome_indicador");
        titleTab.setLargura("100");
        titleTab.addLinha(new LinhaHTML());
        titleTab.getLinhaAtual().setEditable("yes");
        titleTab.getLinhaAtual().addCelula(new CelulaHTML());
        titleTab.getLinhaAtual().getCelulaAtual().setAlinhamento("center");
        titleTab.getLinhaAtual().getCelulaAtual().setId("nome_titulo");
        titleTab.getLinhaAtual().getCelulaAtual().setNowrap(true);
        EstiloHTML titleStile = new EstiloHTML();
        titleStile.setBackgroundColor("#FFFFFF");
        titleStile.setFontSize(14);
        titleStile.setFontFamily("verdana");
        titleStile.setFontColor("#000080");
        titleStile.setFontWeight("bold");
        titleStile.setFontStyle("normal");
        titleStile.setTextDecoration("none");
        titleStile.setAdditionalParameters("cursor: pointer;");
        titleTab.getLinhaAtual().getCelulaAtual().setEstilo(titleStile);
        titleTab.getLinhaAtual().getCelulaAtual().setEditable("yes");
        titleTab.getLinhaAtual().getCelulaAtual().setConteudo(name);
        return titleTab;
    }

    public static void createMascarasHTMLDimensaoLinha(Field dimensionField, MetaDataField dimensionCube, Indicator indicator) {
        if (dimensionField == null || dimensionCube == null) {
            return;
        }

        if (dimensionField.isNavigable()) {
            addSVGLinkToDimensionCube(dimensionCube, dimensionField, "mais", "btMais vect-plus-sign", "Drill Down", 14, 14);
        } else if (dimensionField.isNavigableUpwards()) {
            addSVGLinkToDimensionCube(dimensionCube, dimensionField, "menos", "btMenos vect-minus-sign", "Drill Up", 14, 14);
        }

        String idImagem = dimensionField.getNickname() + dimensionField.getFieldId();
        String svgClass = (indicator.getFilters().getDimensionFilter() != null && indicator.checkFilters(indicator.getFilters().getDimensionFilter(), dimensionField)) ?
                "btFiltro vect-filter-yellow" : "btFiltro vect-filter-silver";
        addSVGLinkToDimensionCube(dimensionCube, dimensionField, idImagem, svgClass, "Filtrar Dimensão", 18, 18);

        if (dimensionField.isNavigableUpwards()) {
            createMascaraHTMLDrillDownDimensao(dimensionField, dimensionCube, indicator.getCode());
        }
    }

    public static void createMascaraHTMLDrillDownDimensao(Field dimensionField, MetaDataField dimensionCube, Integer indicatorCode) {
        if (dimensionField == null || dimensionCube == null) {
            return;
        }

        LinkHTMLTextoDinamico linkTextoDinamico = new LinkHTMLTextoDinamico("data-dimension-value");
        linkTextoDinamico.addParametro("data-code-col", String.valueOf(dimensionField.getFieldId()));
        linkTextoDinamico.addParametro("data-code-indicador", String.valueOf(indicatorCode));

        HTMLLineMask mascaraLinkHTML = new HTMLLineMask("drilldownFiltro", HTMLLineMask.DYNAMIC_TYPE, linkTextoDinamico);
        dimensionCube.addHTMLLineMask(mascaraLinkHTML);
    }

    private static void addSVGLinkToDimensionCube(MetaDataField dimensionCube, Field dimensionField, String id, String svgClass, String svgTitle, int width, int height) {
        LinkHTMLSVGColuna svgHTML = new LinkHTMLSVGColuna(id, svgClass, svgTitle, width, height);
        svgHTML.addParametro("data-code-col", String.valueOf(dimensionField.getFieldId()));
        HTMLLineMask mascaraLinkHTML = new HTMLLineMask("drilldown", HTMLLineMask.VALUE_TYPE_AFTER, svgHTML);
        dimensionCube.addHTMLLineMask(mascaraLinkHTML);
    }

    public static void configureSorting(Field field, MetaDataField campo) {
        LinkHTMLSVGColuna svgHTML = new LinkHTMLSVGColuna("desc", "btOrdena vect-sort-down", "Ordenar de forma decrescente", 14, 14);
        // configure svgHTML
        campo.addHTMLLineMask(new HTMLLineMask("ordenacao", HTMLLineMask.VALUE_TYPE_AFTER, svgHTML));

        svgHTML = new LinkHTMLSVGColuna("asc", "btOrdena vect-sort-up", "Ordenar de forma crescente", 14, 14);
        // configure svgHTML
        campo.addHTMLLineMask(new HTMLLineMask("ordenacao", HTMLLineMask.VALUE_TYPE_AFTER, svgHTML));
    }

}
