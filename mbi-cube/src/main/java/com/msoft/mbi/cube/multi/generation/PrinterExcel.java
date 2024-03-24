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
import com.msoft.mbi.cube.multi.column.TypeDate;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class PrinterExcel implements Printer {

    private HSSFRow currentLine;
    private HSSFWorkbook workbook;
    private short nextCellIndex;
    private int nextLineIndex;
    private int sequenceFieldHelper = 0;
    private short defaultBorderColor = HSSFColor.HSSFColorPredefined.WHITE.getIndex();
    private final String title;
    private final FileOutputStream output;
    private final HSSFSheet sheet;
    private final HashMap<String, Short> dimensionLineCellIndex;
    private final boolean keepMasks;
    private final Map<String, HSSFCellStyle> excelStyle;
    private final String[] decimalPlacesFormat;
    private static final int MAX_DECIMAL_PLACES = 11;
    private final Map<CellProperty, String> columnSpecificProperties;

    public PrinterExcel(String excelFile, boolean keepMasks, String title) throws FileNotFoundException {
        this(new FileOutputStream(new File("C:/" + excelFile)), keepMasks, title);
    }

    public PrinterExcel(FileOutputStream output, boolean keepMasks, String title) {
        this.title = title;
        this.output = output;
        this.dimensionLineCellIndex = new HashMap<>();
        this.keepMasks = keepMasks;
        this.workbook = new HSSFWorkbook();
        this.sheet = workbook.createSheet("Cube");
        this.nextCellIndex = 0;
        this.nextLineIndex = 0;
        this.decimalPlacesFormat = new String[MAX_DECIMAL_PLACES];
        this.decimalPlacesFormat[0] = "#,##0";
        StringBuilder format = new StringBuilder("#,##0.");
        for (int x = 1; x < MAX_DECIMAL_PLACES; x++) {
            format.append("0");
            this.decimalPlacesFormat[x] = format.toString();
        }
        this.excelStyle = new HashMap<>();
        this.columnSpecificProperties = new HashMap<>();
    }

    private short getExcelAlignment(String alignment) {
        if (CellProperty.ALIGNMENT_LEFT.equals(alignment)) {
            return HorizontalAlignment.LEFT.getCode();
        } else if (CellProperty.ALIGNMENT_RIGHT.equals(alignment)) {
            return HorizontalAlignment.RIGHT.getCode();
        } else {
            return HorizontalAlignment.CENTER.getCode();
        }
    }

    @Override
    public void openLine() {
        this.currentLine = this.sheet.createRow(this.nextLineIndex);
        this.nextCellIndex = 0;
        this.nextLineIndex++;
    }

    @Override
    public void addStyle(CellProperty cellProperty, String name) {
        HSSFCellStyle style = this.createStyle(cellProperty);
        if ((name.equals(CellProperty.CELL_PROPERTY_METRIC_DATA_ONE)) || (name.equals(CellProperty.CELL_PROPERTY_METRIC_DATA_TWO))) {
            style.setDataFormat(this.workbook.createDataFormat().getFormat("dd/mm/yyyy"));
        } else if (!cellProperty.getDateMask().isEmpty())
            style.setDataFormat(this.workbook.createDataFormat().getFormat(cellProperty.getDateMask()));

        this.addStyle(name, style);
    }

    public void addStyle(String name, HSSFCellStyle excelStyle) {
        this.excelStyle.put(name, excelStyle);
    }

    private HSSFCellStyle createStyle(CellProperty cellProperty) {
        HSSFCellStyle style = this.workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.forInt(this.getExcelAlignment(cellProperty.getAlignment())));
        HSSFFont font = this.workbook.createFont();
        font.setColor(ColorUtil.getCorExcel(cellProperty.getFontColor()));
        font.setFontName(cellProperty.getFontName());
        font.setFontHeightInPoints((short) cellProperty.getFontSize());
        if (cellProperty.isBold())
            font.setBold(true);
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(ColorUtil.getCorExcel(cellProperty.getBackGroundColor()));
        short borderColor = this.defaultBorderColor;
        if (cellProperty.isSpecificBorder()) {
            borderColor = ColorUtil.getCorExcel(cellProperty.getBorderColor());
        }
        this.setBorda(style, borderColor);
        style.setWrapText(false);
        return style;
    }

    private void setBorda(HSSFCellStyle style, short corBorda) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBottomBorderColor(corBorda);
        style.setLeftBorderColor(corBorda);
        style.setRightBorderColor(corBorda);
        style.setTopBorderColor(corBorda);
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
        this.printColumn(this.excelStyle.get(cellProperty), formattedValue);
    }


    private void printColumn(HSSFCellStyle style, String formattedValue) {
        HSSFCell cell = this.currentLine.createCell(this.nextCellIndex);
        cell.setCellStyle(style);
        cell.setCellValue(new HSSFRichTextString(formattedValue));
        this.nextCellIndex++;
    }

    private void mergeCells(int startLine, int startColumn, int endLine, int endColumn, HSSFCellStyle style) {
        CellRangeAddress region = new CellRangeAddress(startLine, endLine, startColumn, endColumn);

        if (region.getNumberOfCells() > 1) {
            for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
                HSSFRow row = this.sheet.getRow(i);
                if (row == null) {
                    row = this.sheet.createRow(i);
                }
                for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                    HSSFCell cell = row.getCell(j);
                    if (cell == null) {
                        cell = row.createCell(j);
                    }
                    cell.setCellStyle(style);
                }
            }
            this.sheet.addMergedRegion(region);
        }
    }

    private void printColumn(HSSFCellStyle style, Object formattedValue, int colspan, int rowspan, short numCel) {
        HSSFCell cell = this.currentLine.createCell(numCel);
        this.nextCellIndex = (short) (numCel + colspan);
        if (formattedValue instanceof java.sql.Date) {
            cell.setCellValue(new java.util.Date(((java.sql.Date) formattedValue).getTime()));
        } else {
            cell.setCellValue(new HSSFRichTextString((String) formattedValue));
        }

        cell.setCellStyle(style);
        int rowFrom = this.currentLine.getRowNum();
        int colFrom = cell.getColumnIndex();
        int rowTo = (this.currentLine.getRowNum() + rowspan - 1);
        int colTo = (cell.getColumnIndex() + colspan - 1);

        this.mergeCells(rowFrom, colFrom, rowTo, colTo, style);
    }

    private void setBordersToMergedCells(HSSFSheet sheet) {
        int numMerged = sheet.getNumMergedRegions();
        for (int i = 1; i < numMerged; i++) {
            CellRangeAddress mergedRegions = sheet.getMergedRegion(i);
            RegionUtil.setBorderLeft(BorderStyle.THIN, mergedRegions, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, mergedRegions, sheet);
            RegionUtil.setLeftBorderColor(this.defaultBorderColor, mergedRegions, sheet);
            RegionUtil.setRightBorderColor(this.defaultBorderColor, mergedRegions, sheet);
        }
    }

    @Override
    public void printColumn(String cellProperty, String formattedValue, int colspan, int rowspan) {
        this.printColumn(this.excelStyle.get(cellProperty), formattedValue, colspan, rowspan);
    }

    private void printColumn(HSSFCellStyle style, String formattedValue, int colspan, int rowspan) {
        this.printColumn(style, formattedValue, colspan, rowspan, this.nextCellIndex);
    }

    @Override
    public void printDimensionLineValue(String cellProperty, int colspan, int rowspan, Object valor, DimensionMetaData metaData) {
        String mask = metaData.getMetadataField() != null && !metaData.getMetadataField().getFieldMask().isEmpty() && !metaData.getMetadataField().getFieldMask().get(0).getMascara().isEmpty()
                ? metaData.getMetadataField().getFieldMask().get(0).getMascara().replace("'", "") : "dd/mm/yyyy";

        if (metaData.getDataType() instanceof TypeDate) {
            String dateStyleName = cellProperty + "_Data";
            HSSFCellStyle style = this.excelStyle.getOrDefault(dateStyleName, this.copyStyle(this.excelStyle.get(cellProperty)));
            style.setDataFormat(this.workbook.createDataFormat().getFormat(mask));
            this.addStyle(dateStyleName, style);

            HSSFCellStyle newStyle = this.copyStyle(this.excelStyle.get(cellProperty));
            newStyle.setAlignment(HorizontalAlignment.valueOf(metaData.getCellProperty().getAlignment().toUpperCase()));
            this.printColumn(newStyle, valor, colspan, rowspan, (short) (this.dimensionLineCellIndex.get(metaData.getTitle()) + this.sequenceFieldHelper));
        } else {
            HSSFCellStyle newStyle = this.copyStyle(this.excelStyle.get(cellProperty));
            newStyle.setAlignment(HorizontalAlignment.valueOf(metaData.getCellProperty().getAlignment().toUpperCase()));

            this.printColumn(newStyle, metaData.getFormattedValue(valor), colspan, rowspan, (short) (this.dimensionLineCellIndex.get(metaData.getTitle()) + this.sequenceFieldHelper));
        }
        this.sequenceFieldHelper = 0;
    }

    @Override
    public void endPrinting() {
        try {
            this.setBordersToMergedCells(this.sheet);

            for (int i = 0; i <= this.dimensionLineCellIndex.size(); i++) {
                this.sheet.autoSizeColumn(i);
            }

            this.workbook.write(output);
            this.output.close();
            this.workbook = null;
        } catch (IOException e) {
            throw new CubeMathParserException("Não foi possível finalizar a geração do arquivo excel.", e);
        }
    }

    @Override
    public void addColumnHeaderStyle(CellProperty cellProperty, String name) {
        this.addStyle(cellProperty, name);
    }

    @Override
    public void printDimensionLineHeader(DimensionMetaData dimensionMetaData) {
        int decremento = 1;
        if (!dimensionMetaData.hasSequenceFields()) {
            this.printColumn(this.excelStyle.get(CellProperty.CELL_PROPERTY_DIMENSION_HEADER), dimensionMetaData.getTitle());
        } else {
            decremento++;
            this.printColumn(this.excelStyle.get(CellProperty.CELL_PROPERTY_DIMENSION_HEADER), dimensionMetaData.getTitle(), 2, 1);
        }
        this.dimensionLineCellIndex.put(dimensionMetaData.getTitle(), (short) (this.nextCellIndex - decremento));
    }

    public void printTotalPartialHeader(String cellProperty, String value, int colspan, int rowspan, DimensionMetaData dimensionMetaData) {
        this.printColumn(this.excelStyle.get(cellProperty), value, colspan, rowspan, this.dimensionLineCellIndex.get(dimensionMetaData.getTitle()));
    }

    private void printDecimalPercentageValue(String cellProperty, Double value, int decimalPlaces) {
        String styleName = cellProperty + "_" + decimalPlaces + "cd%";
        HSSFCellStyle style = this.excelStyle.get(styleName);
        if (style == null) {
            style = this.copyStyle(this.excelStyle.get(cellProperty), decimalPlaces);
            short format = this.workbook.createDataFormat().getFormat(this.decimalPlacesFormat[decimalPlaces] + "%");
            style.setDataFormat(format);
            this.addStyle(styleName, style);
        }
        this.printDecimalValue(value / 100, style);

    }

    public void printDecimalValue(Double value, HSSFCellStyle style) {
        HSSFCell cell = this.currentLine.createCell(this.nextCellIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        this.nextCellIndex++;
    }

    private void printDecimalValue(String cellProperty, Double valor, int decimalPlaces) {
        String styleName = cellProperty + "_" + decimalPlaces + "cd";
        HSSFCellStyle style = this.excelStyle.get(styleName);
        if (style == null) {
            style = this.copyStyle(this.excelStyle.get(cellProperty), decimalPlaces);
            this.addStyle(styleName, style);
        }
        this.printDecimalValue(valor, style);
    }

    private String getTotalColumnStyle(ColumnMetaData metaData, String newStyle) {
        CellProperty metricCellProperty = metaData.getCellProperty();
        String styleName = this.columnSpecificProperties.get(metricCellProperty);
        String totalStyleName = newStyle + "_" + styleName;
        HSSFCellStyle style = this.excelStyle.get(totalStyleName);
        if (style == null) {
            style = this.copyStyle(this.excelStyle.get(newStyle));
            style.setAlignment(HorizontalAlignment.forInt(this.getExcelAlignment(metricCellProperty.getAlignment() != null ? metricCellProperty.getAlignment() : CellProperty.ALIGNMENT_RIGHT)));
            this.addStyle(totalStyleName, style);
        }
        return totalStyleName;
    }

    private HSSFCellStyle copyStyle(HSSFCellStyle style, int decimalPlaces) {
        HSSFCellStyle newStyle = this.copyStyle(style);
        short format = this.workbook.createDataFormat().getFormat(this.decimalPlacesFormat[decimalPlaces]);
        newStyle.setDataFormat(format);
        return newStyle;
    }

    private HSSFCellStyle copyStyle(HSSFCellStyle style) {
        HSSFCellStyle newStyle = this.workbook.createCellStyle();
        newStyle.setAlignment(style.getAlignment());
        newStyle.setVerticalAlignment(style.getVerticalAlignment());
        newStyle.setFont(style.getFont(this.workbook));
        newStyle.setFillPattern(style.getFillPattern());
        newStyle.setFillForegroundColor(style.getFillForegroundColor());
        this.setBorda(newStyle, style.getTopBorderColor());
        return newStyle;
    }

    @Override
    public void printNumberValue(String cellProperty, Double valor, int decimalNumber) {
        this.printDecimalValue(cellProperty, valor, decimalNumber);
    }

    @Override
    public void startPrinting() {
        this.openLine();
        this.openLine();
        HSSFCell cell = this.currentLine.createCell(nextCellIndex);
        cell.setCellValue(new HSSFRichTextString(this.title));
        CellProperty cellProperty = new CellProperty();
        cellProperty.setBold(true);
        cellProperty.setAlignment(CellProperty.ALIGNMENT_CENTER);
        cellProperty.setFontColor("336699");
        cellProperty.setFontSize(12);
        cellProperty.setFontName("verdana");
        cellProperty.setBackGroundColor("ffffff");
        cellProperty.setBorderColor("ffffff");
        cellProperty.setSpecificBorder(true);
        HSSFCellStyle style = createStyle(cellProperty);
        this.mergeCells(this.currentLine.getRowNum(), 0, this.currentLine.getRowNum(), 3, style);
        this.openLine();
    }

    @Override
    public void setDefaultBorderColor(String corBorda) {
        this.defaultBorderColor = ColorUtil.getCorExcel(corBorda);
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData) {
        this.printColumn(this.excelStyle.get(cellProperty), metaData.getTitle());
        this.dimensionLineCellIndex.put(metaData.getTitle(), (short) (this.nextCellIndex - 1));
    }

    @Override
    public void addColumnSpecificPropertyStyle(CellProperty cellProperty, String name) {
        this.columnSpecificProperties.put(cellProperty, name);

    }

    @Override
    public String getEmptyValue() {
        return "";
    }

    @Override
    public void printSequenceField(DimensionMetaData dimensionMetaData, String sequence, int colspan, int rowspan) {
        this.printColumn(this.excelStyle.get(CellProperty.CELL_PROPERTY_SEQUENCE), sequence, colspan, rowspan, this.dimensionLineCellIndex.get(dimensionMetaData.getTitle()));
        this.sequenceFieldHelper++;
    }

    @Override
    public void printMetricValue(String cellProperty, Double valor, MetricMetaData metaData) {
        String totalStyleName = this.getTotalColumnStyle(metaData, cellProperty);
        if (this.keepMasks) {
            this.printColumn(this.excelStyle.get(totalStyleName), metaData.getFormattedValue(valor));
        } else {
            if (valor != null) {
                if (!metaData.isUsePercent()) {
                    this.printNumberValue(totalStyleName, valor, metaData.getDecimalPlacesNumber());
                } else {
                    this.printPercentNumberValue(totalStyleName, valor, metaData.getDecimalPlacesNumber());
                }
            } else {
                this.printColumn(this.excelStyle.get(totalStyleName), "");
            }
        }
    }

    @Override
    public void printColumnValue(String cellProperty, int colspan, int rowspan, Object valor, ColumnMetaData metaData) {
        this.printColumn(this.excelStyle.get(cellProperty), metaData.getFormattedValue(valor), colspan, rowspan);
    }

    @Override
    public void printColumnValue(String cellProperty, Object value, ColumnMetaData metaData) {
        if (value instanceof Date) {
            this.printColumn(this.excelStyle.get(cellProperty), value, 1, 1, this.nextCellIndex);
        } else {
            this.printColumn(this.excelStyle.get(cellProperty), metaData.getFormattedValue(value));
        }
    }

    @Override
    public void printColumnHeader(String cellProperty, String title) {
        this.printColumn(this.excelStyle.get(cellProperty), title);
    }

    @Override
    public void printColumnHeader(String cellProperty, ColumnMetaData metaData, int colspan, int rowspan) {
        this.printColumn(this.excelStyle.get(cellProperty), metaData.getTitle(), colspan, rowspan);
    }

    @Override
    public void printSequenceField(String sequence) {
        this.printColumn(this.excelStyle.get(CellProperty.CELL_PROPERTY_SEQUENCE), sequence);
    }

    @Override
    public void addLinkStyle(CellProperty cellProperty, String name) {
    }

    @Override
    public void printPercentNumberValue(String name, Double value, int decimalNumber) {
        this.printDecimalPercentageValue(name, value, decimalNumber);
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
