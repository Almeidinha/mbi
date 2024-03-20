package com.msoft.mbi.cube.multi.generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.column.TipoData;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class PrinterExcel implements Printer {

    private String titulo = "";
    private FileOutputStream saida = null;
    private HSSFRow linhaAtual;
    private HSSFSheet planilha;
    private HSSFWorkbook livroPlanilhas;
    private short proximoIndiceCelula;
    private int proximoIndiceLinha;
    private HashMap<String, Short> indicesCelulaDimensoesLinha;
    private boolean mantemMascaras;
    private Map<String, HSSFCellStyle> estilosExcel;
    private String[] formatosCasasDecimais;
    private static final int NUM_MAX_CASAS_DECIMAIS = 11;
    private int auxCampoSequencia = 0;
    private short corBordasPadrao = HSSFColor.HSSFColorPredefined.WHITE.getIndex();
    private Map<CellProperty, String> propriedadesEspecificasColuna;

    public PrinterExcel(String arquivoExcel, boolean manterMascaras, String titulo) throws FileNotFoundException {
        this(new FileOutputStream(new File("C:/" + arquivoExcel)), manterMascaras, titulo);
    }

    public PrinterExcel(FileOutputStream output, boolean manterMascaras, String titulo) {
        this.titulo = titulo;
        this.saida = output;
        this.indicesCelulaDimensoesLinha = new HashMap<String, Short>();
        this.mantemMascaras = manterMascaras;
        this.livroPlanilhas = new HSSFWorkbook();
        this.planilha = livroPlanilhas.createSheet("Cubo");
        this.proximoIndiceCelula = 0;
        this.proximoIndiceLinha = 0;
        this.formatosCasasDecimais = new String[NUM_MAX_CASAS_DECIMAIS];
        this.formatosCasasDecimais[0] = "#,##0";
        StringBuilder formato = new StringBuilder("#,##0.");
        for (int x = 1; x < NUM_MAX_CASAS_DECIMAIS; x++) {
            formato.append("0");
            this.formatosCasasDecimais[x] = formato.toString();
        }
        this.estilosExcel = new HashMap<>();
        this.propriedadesEspecificasColuna = new HashMap<>();
    }

    private short getAlinhamentoExcel(String alinhamento) {
        if (CellProperty.ALIGNMENT_LEFT.equals(alinhamento)) {
            return HorizontalAlignment.LEFT.getCode();
        } else if (CellProperty.ALIGNMENT_RIGHT.equals(alinhamento)) {
            return HorizontalAlignment.RIGHT.getCode();
        } else {
            return HorizontalAlignment.CENTER.getCode();
        }
    }

    @Override
    public void openLine() {
        this.linhaAtual = this.planilha.createRow(this.proximoIndiceLinha);
        this.proximoIndiceCelula = 0;
        this.proximoIndiceLinha++;
    }

    @Override
    public void addStyle(CellProperty cellProperty, String name) {
        HSSFCellStyle estilo = this.criaEstilo(cellProperty);
        if ((name.equals(CellProperty.CELL_PROPERTY_METRIC_DATA_ONE)) || (name.equals(CellProperty.CELL_PROPERTY_METRIC_DATA_TWO))) {
            estilo.setDataFormat(this.livroPlanilhas.createDataFormat().getFormat("dd/mm/yyyy"));
        } else if (!cellProperty.getDateMask().isEmpty())
            estilo.setDataFormat(this.livroPlanilhas.createDataFormat().getFormat(cellProperty.getDateMask()));

        this.adicionaEstilo(name, estilo);
    }

    public void adicionaEstilo(String nomeEstilo, HSSFCellStyle estiloExcel) {
        this.estilosExcel.put(nomeEstilo, estiloExcel);
    }

    private HSSFCellStyle criaEstilo(CellProperty cellProperty) {
        HSSFCellStyle estilo = this.livroPlanilhas.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.forInt(this.getAlinhamentoExcel(cellProperty.getAlignment())));
        HSSFFont fonte = this.livroPlanilhas.createFont();
        fonte.setColor(CorUtil.getCorExcel(cellProperty.getFontColor()));
        fonte.setFontName(cellProperty.getFontName());
        fonte.setFontHeightInPoints((short) cellProperty.getFontSize());
        if (cellProperty.isBold())
            fonte.setBold(true);
        estilo.setFont(fonte);
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setFillForegroundColor(CorUtil.getCorExcel(cellProperty.getBackGroundColor()));
        short corBorda = this.corBordasPadrao;
        if (cellProperty.isSpecificBorder()) {
            corBorda = CorUtil.getCorExcel(cellProperty.getBorderColor());
        }
        this.setBorda(estilo, corBorda);
        estilo.setWrapText(false);
        return estilo;
    }

    private void setBorda(HSSFCellStyle estilo, short corBorda) {
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBottomBorderColor(corBorda);
        estilo.setLeftBorderColor(corBorda);
        estilo.setRightBorderColor(corBorda);
        estilo.setTopBorderColor(corBorda);
    }

    @Override
    public void closeLine() {
    }

    @Override
    public String getNullValue() {
        return "-";
    }

    @Override
    public void printColumn(String cellProperty, String formattedValue) {
        this.printColumn(this.estilosExcel.get(cellProperty), formattedValue);
    }


    private void printColumn(HSSFCellStyle estilo, String valorFormatado) {
        HSSFCell celula = this.linhaAtual.createCell(this.proximoIndiceCelula);
        celula.setCellStyle(estilo);
        celula.setCellValue(new HSSFRichTextString(valorFormatado));
        this.proximoIndiceCelula++;
    }

    private void mesclarCelulas(int linhaInicio, int colunaInicio, int linhaFim, int colunaFim, HSSFCellStyle estilo) {
        CellRangeAddress region = new CellRangeAddress(linhaInicio, linhaFim, colunaInicio, colunaFim);

        if (region.getNumberOfCells() > 1) {
            for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
                HSSFRow row = this.planilha.getRow(i);
                if (row == null)
                    row = this.planilha.createRow(i);
                for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                    HSSFCell cell = row.getCell(j);
                    if (cell == null)
                        cell = row.createCell(j);
                    cell.setCellStyle(estilo);
                }
            }
            this.planilha.addMergedRegion(region);
        }
    }

    private void printColumn(HSSFCellStyle estilo, Object valorFormatado, int colspan, int rowspan, short numCel) {
        HSSFCell celula = this.linhaAtual.createCell(numCel);
        this.proximoIndiceCelula = (short) (numCel + colspan);
        if (valorFormatado instanceof java.sql.Date) {
            celula.setCellValue(new java.util.Date(((java.sql.Date) valorFormatado).getTime()));
        } else {
            celula.setCellValue(new HSSFRichTextString((String) valorFormatado));
        }

        // HSSFCellStyle estilo = this.estilosExcel.get(propriedadeCelula);
        celula.setCellStyle(estilo);
        int rowFrom = this.linhaAtual.getRowNum();
        int colFrom = celula.getColumnIndex();
        int rowTo = (this.linhaAtual.getRowNum() + rowspan - 1);
        int colTo = (celula.getColumnIndex() + colspan - 1);

        this.mesclarCelulas(rowFrom, colFrom, rowTo, colTo, estilo);
    }

    private void setBordersToMergedCells(HSSFSheet sheet) {
        int numMerged = sheet.getNumMergedRegions();
        for (int i = 1; i < numMerged; i++) {
            CellRangeAddress mergedRegions = sheet.getMergedRegion(i);
            RegionUtil.setBorderLeft(BorderStyle.THIN, mergedRegions, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, mergedRegions, sheet);
            RegionUtil.setLeftBorderColor(this.corBordasPadrao, mergedRegions, sheet);
            RegionUtil.setRightBorderColor(this.corBordasPadrao, mergedRegions, sheet);
        }
    }

    @Override
    public void printColumn(String cellProperty, String formattedValue, int colspan, int rowspan) {
        this.printColumn(this.estilosExcel.get(cellProperty), formattedValue, colspan, rowspan);
    }

    private void printColumn(HSSFCellStyle estilo, String valorFormatado, int colspan, int rowspan) {
        this.printColumn(estilo, valorFormatado, colspan, rowspan, this.proximoIndiceCelula);
    }

    @Override
    public void printDimensionLineValue(String cellProperty, int colspan, int rowspan, Object valor, DimensaoMetaData metaData) {

        String mascara = "";
        mascara = (metaData.getCampoMetadadata() == null || metaData.getCampoMetadadata().getFieldMask().isEmpty() || metaData.getCampoMetadadata().getFieldMask().get(0)
                .getMascara().isEmpty() ? "dd/mm/yyyy" : metaData.getCampoMetadadata().getFieldMask().get(0).getMascara()).replace("'", "");

        if (metaData.getTipo() instanceof TipoData) {
            String nomeEstiloData = cellProperty + "_Data";
            HSSFCellStyle estilo = this.estilosExcel.get(nomeEstiloData);
            if (estilo == null) {
                estilo = this.copiaEstilo(this.estilosExcel.get(cellProperty));
                estilo.setDataFormat(this.livroPlanilhas.createDataFormat().getFormat(mascara));

                this.adicionaEstilo(nomeEstiloData, estilo);
            }
            HSSFCellStyle novoStilo = this.copiaEstilo(this.estilosExcel.get(cellProperty));
            novoStilo.setAlignment(HorizontalAlignment.valueOf(metaData.getCellProperty().getAlignment().toUpperCase()));
            this.printColumn(novoStilo, valor, colspan, rowspan, (short) (this.indicesCelulaDimensoesLinha.get(metaData.getTitle()) + this.auxCampoSequencia));
        } else {
            HSSFCellStyle novoStilo = this.copiaEstilo(this.estilosExcel.get(cellProperty));
            novoStilo.setAlignment(HorizontalAlignment.valueOf(metaData.getCellProperty().getAlignment().toUpperCase()));

            this.printColumn(novoStilo, metaData.getFormattedValue(valor), colspan, rowspan, (short) (this.indicesCelulaDimensoesLinha.get(metaData.getTitle()) + this.auxCampoSequencia));
        }
        this.auxCampoSequencia = 0;
    }

    @Override
    public void endPrinting() {
        try {
            this.setBordersToMergedCells(this.planilha);

            for (int i = 0; i <= this.indicesCelulaDimensoesLinha.size(); i++) {
                this.planilha.autoSizeColumn(i);
            }

            this.livroPlanilhas.write(saida);
            this.saida.close();
            this.livroPlanilhas = null;
        } catch (IOException e) {
            CubeMathParserException parserException = new CubeMathParserException("Não foi possível finalizar a geração do arquivo excel.", e);
            throw parserException;
        }
    }

    @Override
    public void addColumnHeaderStyle(CellProperty cellProperty, String name) {
        this.addStyle(cellProperty, name);
    }

    @Override
    public void printDimensionLineHeader(DimensaoMetaData dimensaoMetaData) {
        int decremento = 1;
        if (!dimensaoMetaData.hasSequenceFields()) {
            this.printColumn(this.estilosExcel.get(CellProperty.CELL_PROPERTY_DIMENSION_HEADER), dimensaoMetaData.getTitle());
        } else {
            decremento++;
            this.printColumn(this.estilosExcel.get(CellProperty.CELL_PROPERTY_DIMENSION_HEADER), dimensaoMetaData.getTitle(), 2, 1);
        }
        this.indicesCelulaDimensoesLinha.put(dimensaoMetaData.getTitle(), (short) (this.proximoIndiceCelula - decremento));
    }

    public void printTotalPartialHeader(String cellProperty, String value, int colspan, int rowspan, DimensaoMetaData dimensaoMetaData) {
        this.printColumn(this.estilosExcel.get(cellProperty), value, colspan, rowspan, this.indicesCelulaDimensoesLinha.get(dimensaoMetaData.getTitle()));
    }

    private void imprimeValorDecimalPercentual(String propriedadeCelula, Double valor, int nCasasDecimais) {
        String nomeEstiloCD = propriedadeCelula + "_" + nCasasDecimais + "cd%";
        HSSFCellStyle estilo = this.estilosExcel.get(nomeEstiloCD);
        if (estilo == null) {
            estilo = this.copiaEstilo(this.estilosExcel.get(propriedadeCelula), nCasasDecimais);
            short format = this.livroPlanilhas.createDataFormat().getFormat(this.formatosCasasDecimais[nCasasDecimais] + "%");
            estilo.setDataFormat(format);
            this.adicionaEstilo(nomeEstiloCD, estilo);
        }
        this.imprimeValorDecimal(valor / 100, estilo);

    }

    public void imprimeValorDecimal(Double valor, HSSFCellStyle estilo) {
        HSSFCell celula = this.linhaAtual.createCell(this.proximoIndiceCelula);
        celula.setCellValue(valor.doubleValue());
        celula.setCellStyle(estilo);
        this.proximoIndiceCelula++;
    }

    private void imprimeValorDecimal(String propriedadeCelula, Double valor, int nCasasDecimais) {
        String nomeEstiloCD = propriedadeCelula + "_" + nCasasDecimais + "cd";
        HSSFCellStyle estilo = this.estilosExcel.get(nomeEstiloCD);
        if (estilo == null) {
            estilo = this.copiaEstilo(this.estilosExcel.get(propriedadeCelula), nCasasDecimais);
            this.adicionaEstilo(nomeEstiloCD, estilo);
        }
        this.imprimeValorDecimal(valor, estilo);
    }

    private String getEstiloTotalColuna(ColumnMetaData metaData, String estiloAplicar) {
        CellProperty propriedadeMetrica = metaData.getCellProperty();
        String nomeEstiloEspecifico = this.propriedadesEspecificasColuna.get(propriedadeMetrica);
        String nomeEstiloTotal = estiloAplicar + "_" + nomeEstiloEspecifico;
        HSSFCellStyle estilo = this.estilosExcel.get(nomeEstiloTotal);
        if (estilo == null) {
            estilo = this.copiaEstilo(this.estilosExcel.get(estiloAplicar));
            estilo.setAlignment(HorizontalAlignment.forInt(this.getAlinhamentoExcel(propriedadeMetrica.getAlignment() != null ? propriedadeMetrica.getAlignment() : CellProperty.ALIGNMENT_RIGHT)));
            this.adicionaEstilo(nomeEstiloTotal, estilo);
        }
        return nomeEstiloTotal;
    }

    private HSSFCellStyle copiaEstilo(HSSFCellStyle estilo, int nCasasDecimais) {
        HSSFCellStyle novoEstilo = this.copiaEstilo(estilo);
        short format = this.livroPlanilhas.createDataFormat().getFormat(this.formatosCasasDecimais[nCasasDecimais]);
        novoEstilo.setDataFormat(format);
        return novoEstilo;
    }

    private HSSFCellStyle copiaEstilo(HSSFCellStyle estilo) {
        HSSFCellStyle novoEstilo = this.livroPlanilhas.createCellStyle();
        novoEstilo.setAlignment(estilo.getAlignmentEnum());
        novoEstilo.setVerticalAlignment(estilo.getVerticalAlignmentEnum());
        novoEstilo.setFont(estilo.getFont(this.livroPlanilhas));
        novoEstilo.setFillPattern(estilo.getFillPatternEnum());
        novoEstilo.setFillForegroundColor(estilo.getFillForegroundColor());
        this.setBorda(novoEstilo, estilo.getTopBorderColor());
        return novoEstilo;
    }

    @Override
    public void printNumberValue(String cellProperty, Double valor, int decimalNumber) {
        this.imprimeValorDecimal(cellProperty, valor, decimalNumber);
    }

    @Override
    public void startPrinting() {
        this.openLine();
        this.openLine();
        HSSFCell celula = this.linhaAtual.createCell(proximoIndiceCelula);
        celula.setCellValue(new HSSFRichTextString(this.titulo));
        CellProperty cellProperty = new CellProperty();
        cellProperty.setBold(true);
        cellProperty.setAlignment(CellProperty.ALIGNMENT_CENTER);
        cellProperty.setFontColor("336699");
        cellProperty.setFontSize(12);
        cellProperty.setFontName("verdana");
        cellProperty.setBackGroundColor("ffffff");
        cellProperty.setBorderColor("ffffff");
        cellProperty.setSpecificBorder(true);
        HSSFCellStyle estilo = criaEstilo(cellProperty);
        this.mesclarCelulas(this.linhaAtual.getRowNum(), 0, this.linhaAtual.getRowNum(), 3, estilo);
        this.openLine();
    }

    @Override
    public void setDefaultBorderColor(String corBorda) {
        this.corBordasPadrao = CorUtil.getCorExcel(corBorda);
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData) {
        // propriedadeCelula = this.getEstiloTotalColuna(metaData, propriedadeCelula);
        this.printColumn(this.estilosExcel.get(cellProperty), metaData.getTitle());
        this.indicesCelulaDimensoesLinha.put(metaData.getTitle(), (short) (this.proximoIndiceCelula - 1));
    }

    @Override
    public void addColumnSpecificPropertyStyle(CellProperty cellProperty, String name) {
        this.propriedadesEspecificasColuna.put(cellProperty, name);

    }

    @Override
    public String getEmptyValue() {
        return "";
    }

    @Override
    public void printSequenceField(DimensaoMetaData dimensaoMetaData, String sequence, int colspan, int rowspan) {
        this.printColumn(this.estilosExcel.get(CellProperty.CELL_PROPERTY_SEQUENCE), sequence, colspan, rowspan, (short) (this.indicesCelulaDimensoesLinha.get(dimensaoMetaData.getTitle())));
        this.auxCampoSequencia++;
    }

    @Override
    public void printMetricValue(String cellProperty, Double valor, MetricMetaData metaData) {
        String nomeEstiloTotal = this.getEstiloTotalColuna(metaData, cellProperty);
        if (this.mantemMascaras) {
            this.printColumn(this.estilosExcel.get(nomeEstiloTotal), metaData.getFormattedValue(valor));
        } else {
            if (valor != null) {
                if (!metaData.isUsePercent())
                    this.printNumberValue(nomeEstiloTotal, valor, metaData.getDecimalPlacesNumber());
                else
                    this.printPercentNumberValue(nomeEstiloTotal, valor, metaData.getDecimalPlacesNumber());
            } else {
                this.printColumn(this.estilosExcel.get(nomeEstiloTotal), "");
            }
        }

    }

    @Override
    public void printColumnValue(String cellProperty, int colspan, int rowspan, Object valor, ColumnMetaData metaData) {
        this.printColumn(this.estilosExcel.get(cellProperty), metaData.getFormattedValue(valor), colspan, rowspan);
    }

    @Override
    public void printColumnValue(String cellProperty, Object value, ColumnMetaData metaData) {
        if (value instanceof Date) {
            this.printColumn(this.estilosExcel.get(cellProperty), value, 1, 1, this.proximoIndiceCelula);
        } else {
            this.printColumn(this.estilosExcel.get(cellProperty), metaData.getFormattedValue(value));
        }
    }

    @Override
    public void printColumnHeader(String cellProperty, String title) {
        this.printColumn(this.estilosExcel.get(cellProperty), title);
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData, int colspan, int rowspan) {
        this.printColumn(this.estilosExcel.get(cellProperty), metaData.getTitle(), colspan, rowspan);
    }

    @Override
    public void printSequenceField(String sequence) {
        this.printColumn(this.estilosExcel.get(CellProperty.CELL_PROPERTY_SEQUENCE), sequence);
    }

    @Override
    public void addLinkStyle(CellProperty cellProperty, String name) {
    }

    @Override
    public void printPercentNumberValue(String name, Double value, int decimalNumber) {
        this.imprimeValorDecimalPercentual(name, value, decimalNumber);
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

}
