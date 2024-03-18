package com.msoft.mbi.cube.multi.generation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.*;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLValorDinamicoRenderer;

public class ImpressorHTML implements Impressor {

    private final Writer output;
    private FileOutputStream fos = null;
    private final List<String> classesIndex = new ArrayList<>();
    private final HashMap<String, String> classesCSS = new HashMap<>();
    @SuppressWarnings("unused")
    private String corBordasPadrao = "FFFFFF";
    private final Map<CellProperty, String> propriedadesEspecificasColuna;
    private final AplicadorEfeitoHTML aplicadorEfeitoHTML;

    public ImpressorHTML(String arquivoHTML) {
        try {
            fos = new FileOutputStream("c:\\" + arquivoHTML);
            this.output = new OutputStreamWriter(fos);
            this.propriedadesEspecificasColuna = new HashMap<>();
            this.aplicadorEfeitoHTML = new AplicadorEfeitoHTMLAplica();
        } catch (FileNotFoundException e) {
            throw new CubeMathParserException("Não foi possível criar o arquivo.", e);
        }
    }

    public ImpressorHTML(Writer output, boolean imprimeLinks) {
        this.output = output;
        this.propriedadesEspecificasColuna = new HashMap<>();
        if (imprimeLinks) {
            this.aplicadorEfeitoHTML = new AplicadorEfeitoHTMLAplica();
        } else {
            this.aplicadorEfeitoHTML = new AplicadorEfeitoHTMLNaoAplica();
        }
    }

    private void imprime(String texto) {
        try {
            this.output.write(texto + "\n");
        } catch (IOException e) {
            throw new CubeMathParserException("Não foi possível realizar a impressão do valor.", e);
        }
    }

    public void imprimeColuna(String propriedadeCelula, String valorFormatado) {
        this.imprime("<td class='" + propriedadeCelula + "'>" + valorFormatado + "</td>");
    }

    public void imprimeColuna(String propriedadeCelula, String valorFormatado, int colspan, int rowspan) {
        this.imprime("<td colspan='" + colspan + "' rowspan='" + rowspan + "' class='" + propriedadeCelula + "'>" + valorFormatado + "</td>");
    }

    private void abreTabela() {
        this.imprime("<table class='mainTable'>");
    }

    private void fechaTabela() {
        this.imprime("</table>");
    }

    public String getValorNulo() {
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

    public void finalizaImpressao() {
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

    private String criaEstilo(CellProperty cellProperty, String nomeClasse) {
        StringBuilder descricao = new StringBuilder();

        descricao.append(cellProperty.getBackGroundColor() != null ? ("background-color: #" + cellProperty.getBackGroundColor() + "; ") : "")
                .append(cellProperty.getFontSize() != 0 ? ("font-size: " + cellProperty.getFontSize() + "px; ") : "")
                .append(cellProperty.getFontName() != null ? ("font-family: " + cellProperty.getFontName() + "; ") : "")
                .append(cellProperty.getFontColor() != null ? ("color: #" + cellProperty.getFontColor() + "; ") : "").append(cellProperty.isBold() ? "font-weight: bold; " : "")
                .append(cellProperty.isItalic() ? "font-style: italic; " : "")
                .append(cellProperty.getAlignment() != null ? ("text-align: " + cellProperty.getAlignment() + "; ") : "")
                .append(cellProperty.getSFloat() != null ? ("float: " + cellProperty.getSFloat() + "; ") : "")
                .append(cellProperty.getWidth() != 0 ? ("width: " + cellProperty.getWidth() + "px; ") : "")
                .append(cellProperty.isSpecificBorder() ? ("border: 1px solid #" + cellProperty.getBorderColor() + "; ") : "");

        for (String atributo : cellProperty.getExtraAttributes().keySet()) {
            descricao.append(atributo).append(": ").append(cellProperty.getExtraAttributes().get(atributo)).append("; ");
        }
        return descricao.toString();
    }

    public void adicionaEstilo(CellProperty cellProperty, String nomeEstilo) {
        String estilo = this.criaEstilo(cellProperty, nomeEstilo);

        this.classesIndex.add(nomeEstilo);
        this.classesCSS.put(nomeEstilo, estilo);
    }

    @Override
    public void abreLinha() {
        this.imprime("<tr>");
    }

    @Override
    public void abreLinhaHead() {
        this.imprime("<thead>");
    }

    @Override
    public void abreLinhaBody() {
        this.imprime("<tbody>");
    }

    @Override
    public void fechaLinhaBody() {
        this.imprime("</tbody>");
    }

    @Override
    public void fechaLinhaHead() {
        this.imprime("</thead>");
    }

    @Override
    public void fechaLinha() {
        this.imprime("</tr>");
    }

    private String aplicaEfeitoHTML(Object valor, MascaraRenderer efeitoHTMLDecorator) {
        return this.aplicadorEfeitoHTML.aplicaEfeitoHTML(valor, efeitoHTMLDecorator);
    }

    private String aplicaEfeitoHTMLDinamico(Object valorImprimir, String valorParametro, MascaraLinkHTMLValorDinamicoRenderer efeitoHTMLDecorator) {
        return this.aplicadorEfeitoHTML.aplicaEfeitoHTMLDinamico(valorImprimir, valorParametro, efeitoHTMLDecorator);
    }

    private String getConteudoCabecalhoColuna(ColunaMetaData metaData, int colspan, int rowspan, String estiloCabecalho) {
        StringBuilder sb = new StringBuilder();
        sb.append("	<th colspan='").append(colspan).append("' rowspan='").append(rowspan).append("' class='").append(estiloCabecalho).append("'>");
        sb.append("		<div class='header'>");
        sb.append(this.getValorImprimir(metaData));
        sb.append("		</div>");
        sb.append("	</th>");
        return sb.toString();
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
    public void imprimeCabecalhoTotalParcial(String propriedadeCelula, String valor, int colspan, int rowspan, DimensaoMetaData dimensaoTotalizada) {
        this.imprimeColuna(propriedadeCelula, valor, colspan, rowspan);
    }

    private String getValorCasasDecimais(Double valor, int casasDecimais) {
        NumberFormat numberInstance = NumberFormat.getInstance(Locale.GERMANY);
        numberInstance.setMinimumFractionDigits(casasDecimais);
        numberInstance.setMaximumFractionDigits(casasDecimais);
        return numberInstance.format(valor.doubleValue());
    }

    @Override
    public void imprimeValorNumero(String propriedadeCelula, Double valor, int casasDecimais) {
        this.imprimeColuna(propriedadeCelula, this.getValorCasasDecimais(valor, casasDecimais));
    }

    public void imprimeValorNumeroPercentual(String propriedadeCelula, Double valor, int casasDecimais) {
        String valorAplicar = this.getValorCasasDecimais(valor, casasDecimais) + "%";
        this.imprimeColuna(propriedadeCelula, valorAplicar);
    }

    @Override
    public void iniciaImpressao() {
        this.imprimeCassesCSS();
        this.abreTabela();
    }

    @Override
    public void setCorBordasPadrao(String corBorda) {
        this.corBordasPadrao = corBorda;
    }

    public String getEstiloEspeficoColuna(ColunaMetaData metaData) {
        return this.propriedadesEspecificasColuna.get(metaData.getCellProperty());
    }

    private String getValorImprimir(ColunaMetaData metaData) {
        return this.aplicaEfeitoHTML(metaData.getTitulo(), metaData.getEfeitoHTMLDecorator());
    }

    @Override
    public void imprimeCabecalhoColuna(String propriedadeCelula, ColunaMetaData metaData) {
        this.imprime(this.getConteudoCabecalhoColuna(metaData, 1, 1, propriedadeCelula));
    }

    @Override
    public void imprimeCabecalhoColuna(String propriedadeCelula, ColunaMetaData metaData, int colspan, int rowspan) {
        this.imprime(this.getConteudoCabecalhoColuna(metaData, colspan, rowspan, propriedadeCelula));
    }

    @Override
    public void imprimeCabecalhoColuna(String propriedadeCelula, String tituloColuna) {
        this.imprime("<th class='" + propriedadeCelula + "'>" + tituloColuna + "</th>");
    }

    @Override
    public void adicionaEstiloPropriedadeEspecificaColuna(CellProperty propriedadeMetrica, String nomeEstilo) {
        this.propriedadesEspecificasColuna.put(propriedadeMetrica, nomeEstilo);
        this.adicionaEstilo(propriedadeMetrica, nomeEstilo);
    }

    @Override
    public String getValorVazio() {
        return "&nbsp;";
    }

    @Override
    public void imprimeValorColuna(String propriedadeCelula, int colspan, int rowspan, Object valor, ColunaMetaData metaData) {
        String valorImprimir = this.aplicaEfeitoHTMLDinamico(metaData.getFormattedValue(valor), (metaData.getFormattedValue(valor)), metaData.getEfeitosHTMLValorDecorator());
        this.imprimeColuna(propriedadeCelula + " " + this.getEstiloEspeficoColuna(metaData), valorImprimir, colspan, rowspan);
    }

    @Override
    public void imprimeValorColuna(String propriedadeCelula, Object valor, ColunaMetaData metaData) {
        String valorImprimir = this.aplicaEfeitoHTMLDinamico(metaData.getFormattedValue(valor), (valor != null ? valor.toString() : null), metaData.getEfeitosHTMLValorDecorator());
        this.imprimeColuna(propriedadeCelula + " " + this.getEstiloEspeficoColuna(metaData), valorImprimir);
    }

    @Override
    public void imprimeValorDimensaoLinha(String propriedadeCelula, int colspan, int rowspan, Object valor, DimensaoMetaData metaData) {
        this.imprimeValorColuna(metaData.getEstiloPadrao() + " " + propriedadeCelula, colspan, rowspan, valor, metaData);
    }

    @Override
    public void imprimeValorMetrica(String propriedadeCelula, Double valor, MetricaMetaData metaData) {
        String propriedadeMetrica = this.getEstiloEspeficoColuna(metaData);
        if (propriedadeMetrica != null) {
            propriedadeCelula += " " + propriedadeMetrica;
        }
        if (!metaData.isUtilizaPercentual())
            this.imprimeValorNumero(propriedadeCelula, valor, metaData.getNCasasDecimais());
        else
            this.imprimeValorNumeroPercentual(propriedadeCelula, valor, metaData.getNCasasDecimais());
    }

    @Override
    public void imprimeCampoSequencia(DimensaoMetaData dimensaoMetaData, String sequencia, int colspan, int rowspan) {
        this.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_SEQUENCIA, sequencia, colspan, rowspan);
    }

    @Override
    public void imprimeCampoSequencia(String sequencia) {
        this.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_SEQUENCIA, sequencia);
    }

    @Override
    public void adicionaEstiloLink(CellProperty cellProperty, String nomeEstilo) {
        this.adicionaEstilo(cellProperty, nomeEstilo);
    }

    @Override
    public void adicionaEstiloCabecalhoColuna(CellProperty cellProperty, String nomeEstilo) {
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
        this.adicionaEstilo(cellPropertyCabecalho, nomeEstilo);

    }
}
