package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public abstract class TableGenerator {

    protected Printer printer = null;
    protected Cube cube = null;
    protected int metricsAmount = 0;
    protected List<MetricMetaData> visibleMetrics;
    protected int currentLine = 0;
    private boolean scheduled = false;

    public abstract void process(Printer iPrinter);

    protected void createsSpecificStylesColumns() {
        List<ColumnMetaData> columns = this.cube.getColumnsViewed();
        int index = 0;
        for (ColumnMetaData metaData : columns) {
            String classname = CellProperty.CELL_PROPERTY_PREFIX + index++;
            this.printer.addColumnSpecificPropertyStyle(metaData.getCellProperty(), classname);
        }
    }

    protected void createColorAlertStyles(List<?> alertColors) {
        for (Object colors : alertColors) {
            ColorAlertConditions colorAlertConditions = (ColorAlertConditions) colors;
            this.printer.addStyle(colorAlertConditions.getAlertProperty(), CellProperty.CELL_PROPERTY_ALERTS_PREFIX + colorAlertConditions.getSequence());
        }
    }

    protected void createDefaultStyles() {
        CellProperty cellPropertyOne = createCellProperty(CellProperty.ALIGNMENT_RIGHT, ColorUtil.DARK_BLUE, ColorUtil.GREY_25_PERCENT, CellProperty.FONT_VERDANA, 10, ColorUtil.BLUE, true);
        this.printer.addStyle(cellPropertyOne, CellProperty.CELL_PROPERTY_TOTAL_GENERAL);

        CellProperty metricOneProperty = createCellProperty(CellProperty.ALIGNMENT_RIGHT, ColorUtil.DARK_BLUE, ColorUtil.LIGHT_TURQUOISE, CellProperty.FONT_VERDANA, 10, ColorUtil.BLUE, true);
        this.printer.addStyle(metricOneProperty, CellProperty.CELL_PROPERTY_METRIC_VALUE_ONE);

        CellProperty metricTwoProperty = createCellProperty(CellProperty.ALIGNMENT_RIGHT, ColorUtil.DARK_BLUE, ColorUtil.WHITE, CellProperty.FONT_VERDANA, 10, ColorUtil.BLUE, true);
        this.printer.addStyle(metricTwoProperty, CellProperty.CELL_PROPERTY_METRIC_VALUE_TWO);

        CellProperty dataMetricOneProperty = createCellProperty(CellProperty.ALIGNMENT_RIGHT, ColorUtil.DARK_BLUE, ColorUtil.LIGHT_TURQUOISE, CellProperty.FONT_VERDANA, 10, ColorUtil.BLUE, true);
        this.printer.addStyle(dataMetricOneProperty, CellProperty.CELL_PROPERTY_METRIC_DATA_ONE);

        CellProperty dataMetricTwoProperty = createCellProperty(CellProperty.ALIGNMENT_RIGHT, ColorUtil.DARK_BLUE, ColorUtil.WHITE, CellProperty.FONT_VERDANA, 10, ColorUtil.BLUE, true);
        this.printer.addStyle(dataMetricTwoProperty, CellProperty.CELL_PROPERTY_METRIC_DATA_TWO);

        CellProperty cellPropertyTwo = createCellProperty(CellProperty.ALIGNMENT_CENTER, ColorUtil.WHITE, ColorUtil.BLUE, CellProperty.FONT_VERDANA, 10, ColorUtil.BLACK, true);
        cellPropertyTwo.setWidth(10);
        this.printer.addStyle(cellPropertyTwo, CellProperty.CELL_PROPERTY_SEQUENCE);
    }

    protected CellProperty createCellProperty(String alignment, String fontColor, String backgroundColor,
                                              String fontName, int fontSize, String borderColor, boolean specificBorder) {
        return CellProperty.builder()
                .alignment(alignment)
                .fontColor(fontColor)
                .backGroundColor(backgroundColor)
                .fontName(fontName)
                .fontSize(fontSize)
                .borderColor(borderColor)
                .specificBorder(specificBorder)
                .build();
    }

    protected void openLine() {
        this.printer.openLine();
        this.currentLine++;
    }

    public void setEmail(boolean email) {
        this.scheduled = email;
    }

    public boolean isEmail() {
        return this.scheduled;
    }

}
