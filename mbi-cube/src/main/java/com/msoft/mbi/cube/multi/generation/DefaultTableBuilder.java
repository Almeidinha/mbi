package com.msoft.mbi.cube.multi.generation;

import java.util.*;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.column.TypeDate;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class DefaultTableBuilder extends TableGenerator {

    private Map<String, Object> currentLineValues = new HashMap<>();
    private final Map<Integer, Map<String, String>> currentLineCellProperties = new HashMap<>();
    private boolean hasSequence = false;
    private MetricLine previousMetricLine = null;

    public DefaultTableBuilder(Cube cube) {
        this.cube = cube;
        this.metricsAmount = 0;
        this.visibleMetrics = new ArrayList<>();
        this.populateVisibleMetrics();
        this.populateMaskProperties();
        this.hasSequence = this.cube.getColumnsViewed().get(0).hasSequenceFields();
    }

    public void process(Printer iPrinter) {
        this.printer = iPrinter;
        this.createDefaultStyles();
        this.printer.startPrinting();

        this.printer.openHeadLine();
        this.openLine();
        this.printHeader();
        this.printer.closeLine();
        this.printer.closeHeadLine();

        this.printer.openBodyLine();
        List<Dimension> dimensions = this.cube.getDimensionsLastLevelLines();
        if (!dimensions.isEmpty()) {
            for (Dimension dimension : dimensions) {
                this.divesLineLevel(dimension);
            }
            this.printTotalGeneralLines();
        }
        this.printer.closeBodyLine();
        this.printer.endPrinting();

        this.populateMaskProperties();
    }

    private void printTotalGeneralLines() {
        this.currentLineValues = new HashMap<>();
        DimensionNullColumn dimensionNullColumn = new DimensionNullColumn(this.cube);
        for (MetricMetaData metricMetaData : this.visibleMetrics) {
            String title = metricMetaData.getTitle();
            if (metricMetaData.isTotalLines()) {
                Double valor = metricMetaData.calculaValorTotalParcial(this.cube, dimensionNullColumn);
                this.currentLineValues.put(title, valor);
            }
        }
        this.openLine();
        if (hasSequence) {
            printer.printColumn(CellProperty.CELL_PROPERTY_TOTAL_GENERAL, this.printer.getEmptyValue());
        }

        for (ColumnMetaData column : this.cube.getColumnsViewed()) {
            if (this.currentLineValues.containsKey(column.getTitle())) {
                column.printFieldTypeValue(this.currentLineValues.get(column.getTitle()), CellProperty.CELL_PROPERTY_TOTAL_GENERAL, printer);
            } else {
                this.printer.printColumn(CellProperty.CELL_PROPERTY_TOTAL_GENERAL, this.printer.getEmptyValue());
            }
        }
        this.printer.closeLine();
    }

    private void createColorsAlertStyles() {
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            this.createColorAlertStyles(metaData.getColorAlertCells());
        }
    }

    protected void createDefaultStyles() {
        this.printer.setDefaultBorderColor("3377CC");
        super.createDefaultStyles();

        CellProperty cellProperty = CellProperty.builder()
                .alignment(CellProperty.ALIGNMENT_CENTER)
                .fontColor("FFFFFF")
                .backGroundColor("3377CC")
                .fontName("Verdana")
                .fontSize(10)
                .extraAttributes(Map.of("border", "1px solid #FFFFFF"))
                .build();

        this.printer.addColumnHeaderStyle(cellProperty, CellProperty.CELL_PROPERTY_DEFAULT_HEADER);
        this.printer.addStyle(cellProperty, CellProperty.CELL_PROPERTY_SEQUENCE_HEADER);

        CellProperty headerProperties = CellProperty.builder().extraAttributes(Map.of("cursor", "pointer")).build();

        this.printer.addLinkStyle(headerProperties, CellProperty.CELL_PROPERTY_DEFAULT_HEADER + " span");
        this.printer.addLinkStyle(headerProperties, CellProperty.CELL_PROPERTY_DEFAULT_HEADER + " IMG");
        this.printer.addLinkStyle(headerProperties, CellProperty.CELL_PROPERTY_METRIC_VALUE_ONE + " span");
        this.printer.addLinkStyle(headerProperties, CellProperty.CELL_PROPERTY_METRIC_VALUE_TWO + " span");

        createCustomDateMaskProperties();

        this.createColorsAlertStyles();

        super.createsSpecificStylesColumns();
    }

    private void populateVisibleMetrics() {
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            if (metaData.isViewed()) {
                this.visibleMetrics.add(metaData);
                this.metricsAmount++;
            }
        }
    }

    private void createCustomDateMaskProperties() {
        String mask;

        if (this.printer instanceof PrinterExcel) {
            for (DimensionMetaData metaData : this.cube.getHierarchyLine()) {
                if (metaData.getDataType() instanceof TypeDate) {

                    mask = metaData.getMetadataField().getFieldMask().get(0).getMascara().replace("'", "");

                    this.printer.addStyle(CellProperty.builder()
                            .alignment(CellProperty.ALIGNMENT_RIGHT)
                            .fontColor("000080")
                            .backGroundColor("D7E3F7")
                            .fontName("Verdana")
                            .fontSize(10)
                            .borderColor("3377CC")
                            .specificBorder(true)
                            .dateMask(mask)
                            .build(), metaData.getTitle() + "1");


                    this.printer.addStyle(CellProperty.builder()
                            .alignment(CellProperty.ALIGNMENT_RIGHT)
                            .fontColor("000080")
                            .backGroundColor("FFFFFF")
                            .fontName("Verdana")
                            .fontSize(10)
                            .borderColor("3377CC")
                            .specificBorder(true)
                            .dateMask(mask)
                            .build(), metaData.getTitle() + "2");

                    this.currentLineCellProperties.get(0).put(metaData.getTitle(), metaData.getTitle() + "1");
                    this.currentLineCellProperties.get(1).put(metaData.getTitle(), metaData.getTitle() + "2");
                }
            }
        }
    }

    private void populateMaskProperties() {
        Map<String, String> firstProperties = new HashMap<>();
        Map<String, String> secondProperties = new HashMap<>();

        for (DimensionMetaData metaData : this.cube.getHierarchyLine()) {
            if (metaData.getDataType() instanceof TypeDate) {
                firstProperties.put(metaData.getTitle(), CellProperty.CELL_PROPERTY_METRIC_DATA_ONE);
                secondProperties.put(metaData.getTitle(), CellProperty.CELL_PROPERTY_METRIC_DATA_TWO);
            } else {
                firstProperties.put(metaData.getTitle(), CellProperty.CELL_PROPERTY_METRIC_VALUE_ONE);
                secondProperties.put(metaData.getTitle(), CellProperty.CELL_PROPERTY_METRIC_VALUE_TWO);
            }
        }
        this.currentLineCellProperties.put(0, firstProperties);
        this.currentLineCellProperties.put(1, secondProperties);
    }

    private String searchPropToApplyOnCell(String propToApply, Object value, ColumnMetaData metaData) {
        String propriedadeAlertaMetrica = null;
        List<?> colorsAlert = metaData.getColorAlertCells();
        for (Object alert : colorsAlert) {
            ColorAlertConditions alertConditions = (ColorAlertConditions) alert;
            if (alertConditions.testCondition(value)) {
                propriedadeAlertaMetrica = CellProperty.CELL_PROPERTY_ALERTS_PREFIX + alertConditions.getSequence();
            }
        }
        if (propriedadeAlertaMetrica != null) {
            propToApply = propriedadeAlertaMetrica;
        }
        return propToApply;
    }

    private void printLine(Map<String, String> propertiesMap) {
        for (ColumnMetaData column : this.cube.getColumnsViewed()) {
            column.printFieldTypeValue(this.currentLineValues.get(column.getTitle()),
                    propertiesMap.get(column.getTitle()), printer);
        }
    }

    private void printHeader() {
        if (this.hasSequence) {
            this.printer.printColumnHeader(CellProperty.CELL_PROPERTY_SEQUENCE_HEADER, "Seq");
        }
        for (ColumnMetaData column : this.cube.getColumnsViewed()) {
            this.printer.printColumnHeader(CellProperty.CELL_PROPERTY_DEFAULT_HEADER, column);
        }
    }

    private void divesLineLevel(Dimension dimensionLine) {
        this.openLine();
        DimensionMetaData metaData = dimensionLine.getMetaData();
        Dimension dimension = dimensionLine;
        while (metaData != null) {
            currentLineValues.put(metaData.getTitle(), dimension.getVisualizationValue());
            metaData = metaData.getParent();
            dimension = dimension.getParent();
        }
        this.printCurrentLine(dimensionLine);
        this.printer.closeLine();
    }

    private void printCurrentLine(Dimension dimensionLine) {
        MetricsMap metricsMap = this.cube.getMetricsMap();
        MetricLine metricLine = metricsMap.getMetricLine(dimensionLine);

        Map<String, Metric> metrics = metricLine.getMetrics();
        String currentLineProps = dimensionLine.getMetricDefaultStyles(this.currentLine);

        Map<String, String> propsmap = this.currentLineCellProperties.get(this.currentLine % 2);
        for (MetricMetaData metricMetaData : this.visibleMetrics) {
            String title = metricMetaData.getTitle();
            Double valor = metrics.get(title).getValue(metricsMap, metricLine, this.previousMetricLine);
            this.currentLineValues.put(title, valor);
            String metricPropToApply = this.searchPropToApplyOnCell(currentLineProps, valor, metricMetaData);
            propsmap.put(title, metricPropToApply);
        }
        String rankingSequence = Optional.ofNullable(dimensionLine.getRankingSequence()).map(String::valueOf).orElse(this.printer.getEmptyValue());
        if (hasSequence) {
            this.printer.printSequenceField(rankingSequence);
        }
        this.printLine(propsmap);

        this.previousMetricLine = metricLine;

    }

}