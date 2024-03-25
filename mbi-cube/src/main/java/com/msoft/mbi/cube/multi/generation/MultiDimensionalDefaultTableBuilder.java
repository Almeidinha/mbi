package com.msoft.mbi.cube.multi.generation;

import java.util.*;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;
import com.msoft.mbi.cube.multi.dimension.DimensionLine;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAHEvolutionMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedFunctionMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class MultiDimensionalDefaultTableBuilder extends TableGenerator {
    private final List<MetricMetaData> metricTotalColumnSum;
    private List<MetricMetaData> metricTotalColumnGeneralSum;
    private final List<MetricMetaData> metricTotalColumnMedia;
    private final List<MetricMetaData> metricsAH;
    private PrintCurrentMetricLine metricLinePrinter = null;
    private final DimensionNullColumn dimensionNullColumn;
    private String currentLineMetricAlert;
    private final List<Dimension> lestLevelDimensionColumns;

    private Dimension previousLineDimension = null;

    public MultiDimensionalDefaultTableBuilder(Cube cube) {
        this.cube = cube;
        dimensionNullColumn = new DimensionNullColumn(cube);
        this.metricsAmount = 0;
        this.visibleMetrics = new ArrayList<>();
        this.metricTotalColumnSum = new ArrayList<>();
        this.metricTotalColumnGeneralSum = new ArrayList<>();
        this.metricTotalColumnMedia = new ArrayList<>();
        this.metricsAH = new ArrayList<>();
        this.lestLevelDimensionColumns = this.cube.getDimensionsLastLevelColumns();
        this.populateVisualizedMetrics();
        this.metricLinePrinter = new PrintCurrentMetricLine(this.visibleMetrics);
    }

    private void processEmptyTable() {
        this.printer.startPrinting();
        this.openLine();
        this.printer.printColumn(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, "A pesquisa não retornou dados");
        this.printer.closeLine();
        this.printer.endPrinting();
    }

    public void process(Printer iPrinter) {
        this.printer = iPrinter;
        if (!cube.getDimensionsLine().isEmpty()) {
            this.createDefaultStyles();
            this.printer.startPrinting();
            this.printer.openHeadLine();
            int printLevel = 0;
            int sequenceLineColspanLevel = this.cube.getMetaData().getLowerLevelSequenceCount();
            int columnMaxExpansion = this.cube.getHierarchyColumn().size();
            int checkProcess = 0;
            for (DimensionMetaData metadata : this.cube.getHierarchyColumn()) {
                checkProcess++;
                if (checkProcess % 100 == 0) {
                    if (this.cube.getCubeListener().stopProcess())
                        return;
                }
                this.openLine();
                this.printer.printColumnHeader(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, metadata, sequenceLineColspanLevel, 1);

                if (!this.cube.getDimensionsColumn().isEmpty()) {
                    Collection<Dimension> set = this.cube.getDimensionsColumn().values();
                    Iterator<Dimension> it = set.iterator();
                    this.diveInColumnLevel(it, printLevel, CellProperty.CELL_PROPERTY_DIMENSION_VALUE);
                }

                printLevel++;
                if (printLevel == 1) {
                    this.printTotalColumnHeader();
                    this.printMetricTotalGeneralColumnHeader();
                } else if (printLevel == columnMaxExpansion) {
                    break;
                }
                this.printer.closeLine();
            }

            this.openLine();
            this.printDimensionLineHeader();

            this.printMetricHeaders(this.cube);
            this.printHeaderMetricsTotalColumns();

            this.printer.closeLine();
            this.printer.closeHeadLine();

            this.printer.openBodyLine();
            if (!this.cube.getDimensionsLine().isEmpty()) {
                Collection<Dimension> set = ((Dimensions) this.cube.getDimensionsLine().clone()).values();
                Iterator<Dimension> it = set.iterator();
                this.openLine();
                this.diveInLineLevel(it, null);
                this.printTotalGeneralLines(sequenceLineColspanLevel);
            }
            this.printer.closeBodyLine();
            this.printer.endPrinting();

        } else {
            this.printer.setDefaultBorderColor("3377CC");
            createDimensionHeader();
            this.processEmptyTable();
        }
    }

    private void createsStylesAlertsOfColorsLink(List<?> colorAlerts) {
        for (Object colorAlert : colorAlerts) {
            ColorAlertConditions alerts = (ColorAlertConditions) colorAlert;
            this.printer.addStyle(alerts.getAlertProperty(), CellProperty.CELL_PROPERTY_ALERTS_PREFIX + alerts.getSequence());
            CellProperty cellProperty = new CellProperty();
            cellProperty.addExtraAttributes("cursor", "pointer");
            this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_ALERTS_PREFIX + alerts.getSequence() + " span");
        }
    }

    protected void openLine() {
        super.openLine();
        this.currentLineMetricAlert = null;
    }

    private void createColorAlertStyles() {
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            this.createColorAlertStyles(metaData.getColorAlertCells());
            this.createColorAlertStyles(metaData.getColorsAlertLines());
        }
        for (DimensionMetaData metaData : this.cube.getHierarchyColumn()) {
            this.createColorAlertStyles(metaData.getColorAlertCells());
            this.createColorAlertStyles(metaData.getColorAlertLines());
        }
        for (DimensionMetaData metaData : this.cube.getHierarchyLine()) {
            this.createsStylesAlertsOfColorsLink(metaData.getColorAlertCells());
            this.createsStylesAlertsOfColorsLink(metaData.getColorAlertLines());
        }
    }

    private void createDimensionHeader() {
        CellProperty cellProperty = new CellProperty();
        cellProperty.setFontColor("ffffff");
        cellProperty.setBackGroundColor("3377CC");
        cellProperty.setFontName("Verdana");
        cellProperty.setBold(true);
        cellProperty.setFontSize(10);
        cellProperty.setSpecificBorder(true);
        cellProperty.setBorderColor("000000");

        this.printer.addColumnHeaderStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_HEADER);
    }

    protected void createDefaultStyles() {
        this.printer.setDefaultBorderColor("3377CC");
        super.createDefaultStyles();
        CellProperty propriedadeDimensaoPadrao = new CellProperty();
        propriedadeDimensaoPadrao.setAlignment(CellProperty.ALIGNMENT_LEFT);
        propriedadeDimensaoPadrao.setFontColor("000080");
        propriedadeDimensaoPadrao.setBackGroundColor("A2C8E8");
        propriedadeDimensaoPadrao.setFontName("Verdana");
        propriedadeDimensaoPadrao.setFontSize(10);
        propriedadeDimensaoPadrao.setSpecificBorder(true);
        propriedadeDimensaoPadrao.setBorderColor("3377CC");
        propriedadeDimensaoPadrao.addExtraAttributes("border-top", "none");
        propriedadeDimensaoPadrao.addExtraAttributes("border-left", "none");
        this.printer.addStyle(propriedadeDimensaoPadrao, CellProperty.CELL_PROPERTY_DIMENSION_VALUE);

        CellProperty cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_VALUE + " span");

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALIGNMENT_RIGHT);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.printer.addStyle(cellProperty, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES);

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALIGNMENT_LEFT);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.printer.addStyle(cellProperty, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER);

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALIGNMENT_CENTER);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.printer.addStyle(cellProperty, CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER);

        createDimensionHeader();

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_HEADER + " span");

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_HEADER + " IMG");

        CellProperty propriedadeCabecalhoMetrica = new CellProperty();
        propriedadeCabecalhoMetrica.setAlignment(CellProperty.ALIGNMENT_CENTER);
        propriedadeCabecalhoMetrica.setFontColor("000080");
        propriedadeCabecalhoMetrica.setBackGroundColor("A2C8E8");
        propriedadeCabecalhoMetrica.setFontName("Verdana");
        propriedadeCabecalhoMetrica.setBold(true);
        propriedadeCabecalhoMetrica.setFontSize(10);
        propriedadeCabecalhoMetrica.setSpecificBorder(true);
        propriedadeCabecalhoMetrica.setBorderColor("3377CC");
        this.printer.addColumnHeaderStyle(propriedadeCabecalhoMetrica, CellProperty.CELL_PROPERTY_METRIC_HEADER);

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_METRIC_HEADER + " span");

        CellProperty cellPropertyValorMetricaOutros = new CellProperty();
        cellPropertyValorMetricaOutros.setAlignment(CellProperty.ALIGNMENT_LEFT);
        cellPropertyValorMetricaOutros.setFontColor("000080");
        cellPropertyValorMetricaOutros.setBackGroundColor("A2C8E8");
        cellPropertyValorMetricaOutros.setFontName("Verdana");
        cellPropertyValorMetricaOutros.setFontSize(10);
        cellPropertyValorMetricaOutros.addExtraAttributes("border-left", "none");
        cellPropertyValorMetricaOutros.addExtraAttributes("border-right", "1px solid #3377CC");
        cellPropertyValorMetricaOutros.setBorderColor("3377CC");
        cellPropertyValorMetricaOutros.setSpecificBorder(true);
        this.printer.addStyle(cellPropertyValorMetricaOutros, CellProperty.CELL_PROPERTY_OTHERS);

        this.createColorAlertStyles();

        super.createsSpecificStylesColumns();
    }

    private void populateVisualizedMetrics() {
        boolean hasDimensionColumn = !this.cube.getHierarchyColumn().isEmpty();
        this.metricTotalColumnGeneralSum = this.cube.getMetricsTotalHorizontal();
        int verificaProcessamento = 0;
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            verificaProcessamento++;
            if (verificaProcessamento % 100 == 0) {
                if (this.cube.getCubeListener().stopProcess())
                    return;
            }
            if (metaData.isViewed()) {
                if (metaData.isTotalSumColumns()) {
                    this.metricTotalColumnSum.add(metaData);
                }
                if (metaData.isTotalMediaColumns()) {
                    this.metricTotalColumnMedia.add(metaData);
                }

                if (metaData instanceof MetricCalculatedFunctionMetaData metrics) {
                    if (metrics.getFieldFunction().equals(MetricCalculatedAHEvolutionMetaData.AH)) {
                        if (hasDimensionColumn) {
                            this.metricsAH.add(metaData);
                            this.visibleMetrics.add(metaData);
                        }
                    } else {
                        this.visibleMetrics.add(metaData);
                    }
                } else {
                    this.visibleMetrics.add(metaData);
                }
                this.metricsAmount++;
            }
        }
    }

    private void printTotalColumnHeader() {
        if (!this.metricTotalColumnSum.isEmpty()) {
            this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Acumulado", this.metricTotalColumnSum.size(), this.cube.getHierarchyColumn().size());
        }
        if (!this.metricTotalColumnMedia.isEmpty()) {
            this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Média", this.metricTotalColumnMedia.size(), this.cube.getHierarchyColumn().size());
        }
    }

    private void printMetricTotalGeneralColumnHeader() {
        if (!this.metricTotalColumnGeneralSum.isEmpty()) {
            this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Total", 1, this.cube.getHierarchyColumn().size() + 1);
        }
    }

    private void printTotalGeneralLines(int colspanSequenceLineLevels) {
        this.openLine();
        this.printer.printColumn(CellProperty.CELL_PROPERTY_TOTAL_GENERAL, this.printer.getEmptyValue(), colspanSequenceLineLevels, 1);
        List<String> alertFunctions = new ArrayList<>();
        alertFunctions.add(MetricMetaData.TOTAL_GENERAL);

        String totalGeneralProp = CellProperty.CELL_PROPERTY_TOTAL_GENERAL;
        String metricsPropertyAlertsRowFunctionsTotalColumns = this.cube.searchMetricsPropertyAlertsRowFunctionsTotalColumns(this.visibleMetrics, alertFunctions);
        if (metricsPropertyAlertsRowFunctionsTotalColumns != null) {
            totalGeneralProp = metricsPropertyAlertsRowFunctionsTotalColumns;
            this.currentLineMetricAlert = metricsPropertyAlertsRowFunctionsTotalColumns;
        }
        alertFunctions.add(MetricMetaData.TOTAL_AV);
        this.printMetrics(this.cube, totalGeneralProp, new PrintMetricLineTotalLines(this.visibleMetrics, alertFunctions), alertFunctions, MetricMetaData.TOTAL_AV);
        this.printer.closeLine();
    }

    private List<MetricMetaData> getMetricsWithoutAH(List<MetricMetaData> metricMetaData) {
        List<MetricMetaData> resultMetrics = new ArrayList<>(metricMetaData);
        resultMetrics.removeAll(this.metricsAH);
        return resultMetrics;
    }

    private void printMetricHeaders(List<MetricMetaData> metrics) {
        for (MetricMetaData metadata : metrics) {
            this.printer.printColumnHeader(CellProperty.CELL_PROPERTY_METRIC_HEADER, metadata);
        }
    }

    private void printHeaderMetricsTotalColumns() {
        if (this.cube.getHierarchyColumn().isEmpty()) {
            for (MetricMetaData metaData : this.metricTotalColumnSum) {
                this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Acumulado " + metaData.getTitle());
            }
            for (MetricMetaData metaData : this.metricTotalColumnMedia) {
                this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Média " + metaData.getTitle());
            }
            this.printMetricTotalGeneralColumnHeader();
        } else {
            this.printMetricHeaders(this.metricTotalColumnSum);
            this.printMetricHeaders(this.metricTotalColumnMedia);
        }

    }

    private void printMetricHeaders(Dimension parentDimension) {
        if (!this.cube.getHierarchyColumn().isEmpty()) {
            List<MetricMetaData> metricsWithoutAH = this.getMetricsWithoutAH(this.visibleMetrics);
            for (Dimension dimension : parentDimension.getDimensionsColumn().values()) {
                if (this.cube.getCubeListener().stopProcess())
                    return;
                if (!dimension.getDimensionsColumn().isEmpty()) {
                    this.printMetricHeaders(dimension);
                } else {
                    List<MetricMetaData> metricasImprimir = this.visibleMetrics;
                    if (dimension.isFirstDimensionColumnSameLevel()) {
                        metricasImprimir = metricsWithoutAH;
                    }
                    this.printMetricHeaders(metricasImprimir);
                }
            }
            if (parentDimension.getMetaData().isTotalPartial()) {
                List<MetricMetaData> metricasImprimir = this.visibleMetrics;
                if (parentDimension.isFirstDimensionColumnSameLevel()) {
                    metricasImprimir = metricsWithoutAH;
                }
                this.printMetricHeaders(metricasImprimir);
            }
        } else {
            this.printMetricHeaders(this.visibleMetrics);
        }
    }

    private void printDimensionLineHeader() {
        for (DimensionMetaData metaData : this.cube.getHierarchyLine()) {
            this.printer.printDimensionLineHeader(metaData);
        }
    }

    private void printTotalPartialLines(Dimension dimensionLine) {
        this.openLine();
        this.printer.printTotalPartialHeader(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER,
                "Total", (dimensionLine.getMetaData().getLowerLevelSequenceCount()), 1, dimensionLine.getMetaData().getChild());
        List<String> alertFunctions = new ArrayList<String>();
        alertFunctions.add(MetricMetaData.TOTAL_PARTIAL);
        this.printMetrics(dimensionLine, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES,
                new PrintMetricLineTotalPartialLines(this.visibleMetrics), alertFunctions, MetricMetaData.TOTAL_PARTIAL);
        this.printer.closeLine();
    }

    private void printMediaPartialLines(Dimension dimensionLine) {
        this.openLine();
        this.printer.printTotalPartialHeader(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER,
                "Média", (dimensionLine.getMetaData().getLowerLevelSequenceCount()), 1, dimensionLine.getMetaData().getChild());
        List<String> alertFunctions = new ArrayList<>();
        alertFunctions.add(MetricMetaData.MEDIA_PARTIAL);
        this.printMetrics(dimensionLine, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES,
                new PrintMetricLineMediaPartialLines(this.visibleMetrics), alertFunctions, MetricMetaData.MEDIA_PARTIAL);
        this.printer.closeLine();
    }

    private void printPartialExpressionLine(Dimension dimensionLine) {
        this.openLine();
        this.printer.printTotalPartialHeader(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER,
                "Expressão", (dimensionLine.getMetaData().getLowerLevelSequenceCount()), 1, dimensionLine.getMetaData().getChild());
        List<String> alertFunctions = new ArrayList<>();
        alertFunctions.add(MetricMetaData.EXPRESSION_PARTIAL);
        this.printMetrics(dimensionLine, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES,
                new PrintMetricLinePartialExpressionLines(this.visibleMetrics), alertFunctions, MetricMetaData.EXPRESSION_PARTIAL); // cria o objeto ImpressaoMetricaLinhaTotalizacaoParcialLinhas que vai fazer o calculo
        this.printer.closeLine();
    }

    private void printTotalPartialColumnHeader(Dimension dimensionLine) {
        int qtdMetricas = this.metricsAmount;
        if (dimensionLine.isFirstDimensionColumnSameLevel()) {
            qtdMetricas = this.getMetricsWithoutAH(this.visibleMetrics).size();
        }
        this.printer.printColumn(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER, "Total", qtdMetricas, (dimensionLine.getMetaData().getLowerLevelsCount()));
    }

    private void diveInColumnLevel(Iterator<Dimension> it, int printlevel, String previousCellProperty) {
        Dimension dimension = null;
        int i;
        for (i = 0; it.hasNext(); i++) {
            if (i % 100 == 0) {
                if (this.cube.getCubeListener().stopProcess())
                    return;
            }
            dimension = it.next();
            String propriedadeAplicar = previousCellProperty;
            String currentDimensionAlertLineProperty = dimension.searchDimensionAlertLineProperty();
            if (currentDimensionAlertLineProperty != null) {
                propriedadeAplicar = currentDimensionAlertLineProperty;
            }

            if (dimension.getLevel() == printlevel) {
                String currentDimensionAlertCellProperty = dimension.searchDimensionAlertCellProperty();
                if (currentDimensionAlertCellProperty != null) {
                    this.printDimensionColumn(dimension, currentDimensionAlertCellProperty, i);
                } else {
                    this.printDimensionColumn(dimension, propriedadeAplicar, i);
                }
            } else if (dimension.getLevel() < printlevel) {
                this.diveInColumnLevel(dimension.getDimensionsColumn().values().iterator(), printlevel, propriedadeAplicar);
            }
        }
        if (Objects.requireNonNull(dimension).getLevel() == printlevel && dimension.getParent().getMetaData().isTotalPartial()) {
            this.printTotalPartialColumnHeader(dimension.getParent());
        }
    }

    private void diveInLineLevel(Iterator<Dimension> it, String previousLevelCellProperty) {
        Dimension dimension;
        String property;
        int i;
        for (i = 0; it.hasNext(); i++) {
            if (i % 100 == 0) {
                if (this.cube.getCubeListener().stopProcess())
                    return;
            }
            dimension = it.next();
            if (i != 0) {
                this.openLine();
            }
            property = dimension.getMetaData().getDefaultStyle();

            if (previousLevelCellProperty != null) {
                property = previousLevelCellProperty;
            }

            String propriedadeCelulaNivelAnteriorAplicar = previousLevelCellProperty;
            String currentDimensionAlertLineProperty = dimension.searchDimensionAlertLineProperty();
            if (currentDimensionAlertLineProperty != null) {
                property = currentDimensionAlertLineProperty;
                propriedadeCelulaNivelAnteriorAplicar = currentDimensionAlertLineProperty;
            }

            String currentDimensionAlertCellProperty = dimension.searchDimensionAlertCellProperty();
            if (currentDimensionAlertCellProperty != null) {
                property = currentDimensionAlertCellProperty;
            }
            this.printDimensionLine(dimension, property, (i + 1));
            if (!dimension.getDimensionsLine().isEmpty()) {

                Dimensions dims = (Dimensions) dimension.getDimensionsLine().clone();

                Iterator<Dimension> dimensionIterator = dims.values().iterator();
                this.diveInLineLevel(dimensionIterator, propriedadeCelulaNivelAnteriorAplicar);

                if (dimension.getMetaData().isTotalPartial() || dimension.getMetaData().isPartialTotalExpression()) {
                    this.printTotalPartialLines(dimension);
                }
                if (dimension.getMetaData().isMediaPartial()) {
                    this.printMediaPartialLines(dimension);
                }
                if (dimension.getMetaData().isExpressionPartial()) {
                    this.printPartialExpressionLine(dimension);
                }

            } else {
                String propertyAlertColorsMetricLineFunctions = dimension
                        .searchMetricsPropertyAlertsRowFunctionsTotalColumns(this.visibleMetrics, ColorAlertMetadata.getHorizaontalToalFunctionList());
                if (propertyAlertColorsMetricLineFunctions != null) {
                    propriedadeCelulaNivelAnteriorAplicar = propertyAlertColorsMetricLineFunctions;
                    this.currentLineMetricAlert = propertyAlertColorsMetricLineFunctions;
                }
                this.printMetrics(dimension, propriedadeCelulaNivelAnteriorAplicar, metricLinePrinter,
                        ColorAlertMetadata.getHorizaontalToalFunctionList(), ColorAlertMetadata.NO_FUNCTION);
                this.printer.closeLine();
            }
        }
    }

    private void printMetricValuesTotalPartialColumns(Dimension dimensionLine, Dimension dimensionColumn,
                                                      MetricLinePrinter impressao) {
        if (dimensionColumn.getMetaData().isTotalPartial()) {
            List<MetricMetaData> oldMetric = impressao.getMetricMetaData();
            if (dimensionColumn.isFirstDimensionColumnSameLevel()) {
                impressao.setMetricMetaData(this.getMetricsWithoutAH(oldMetric));
            }
            String propriedadeCelula = CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES;
            if (this.currentLineMetricAlert != null) {
                propriedadeCelula = this.currentLineMetricAlert;
            }
            impressao.printMetricValues(dimensionColumn, null, dimensionLine, propriedadeCelula,
                    this.printer, this.cube, CalculationSummaryType.TOTAL);
            impressao.setMetricMetaData(oldMetric);
        }
    }

    private Dimension checkPrintTotalPartialColumn(Dimension dimensionLine, Dimension previousParent,
                                                   Dimension current, MetricLinePrinter printer) {
        if (!current.getParent().equals(previousParent)) {
            this.printMetricValuesTotalPartialColumns(dimensionLine, previousParent, printer);
            previousParent = this.checkPrintTotalPartialColumn(dimensionLine, previousParent.getParent(), current.getParent(), printer);
        }
        return current.getParent();
    }

    private void printMetrics(Dimension dimensionLines, String previousLevelCellProps,
                              MetricLinePrinter metricLinePrinter, List<String> metricCellAlerts,
                              String metricLineSearchFunction) {

        String propriedadeAplicar = dimensionLines.getMetricDefaultStyles(this.currentLine);
        if (previousLevelCellProps != null) {
            propriedadeAplicar = previousLevelCellProps;
            this.currentLineMetricAlert = previousLevelCellProps;
        }
        String metricAlertLineProperty;
        if (!this.cube.getHierarchyColumn().isEmpty()) {
            metricAlertLineProperty = dimensionLines.searchMetricAlertLineProperty(this.visibleMetrics, metricLineSearchFunction, this.lestLevelDimensionColumns);
            if (metricAlertLineProperty != null) {
                propriedadeAplicar = metricAlertLineProperty;
                this.currentLineMetricAlert = metricAlertLineProperty;
            }
            Dimension currentParentDimension = this.lestLevelDimensionColumns.get(0).getParent();
            for (int x = 0; x < this.lestLevelDimensionColumns.size(); x++) {
                if (x % 100 == 0) {
                    if (this.cube.getCubeListener().stopProcess())
                        return;
                }
                Dimension lastDimensionColumn = this.lestLevelDimensionColumns.get(x);
                List<MetricMetaData> oldMetrics = metricLinePrinter.getMetricMetaData();
                List<MetricMetaData> metricasImprimir = oldMetrics;
                if (x == 0) {
                    metricasImprimir = this.getMetricsWithoutAH(oldMetrics);
                }
                metricLinePrinter.setMetricMetaData(metricasImprimir);
                currentParentDimension = checkPrintTotalPartialColumn(dimensionLines, currentParentDimension, lastDimensionColumn, metricLinePrinter);
                metricLinePrinter.printMetricValues(dimensionLines, this.previousLineDimension, lastDimensionColumn,
                        propriedadeAplicar, this.printer, cube, metricLinePrinter instanceof PrintMetricLineMediaPartialLines ? CalculationSummaryType.MEDIA : CalculationSummaryType.NORMAL);
                metricLinePrinter.setMetricMetaData(oldMetrics);
            }
            currentParentDimension = this.lestLevelDimensionColumns.get(this.lestLevelDimensionColumns.size() - 1).getParent();
            while (currentParentDimension != null) {
                this.printMetricValuesTotalPartialColumns(dimensionLines, currentParentDimension, metricLinePrinter);
                currentParentDimension = currentParentDimension.getParent();
            }
        } else {
            metricAlertLineProperty = dimensionLines.searchMetricAlertLineProperty(
                    this.visibleMetrics, metricLineSearchFunction, dimensionNullColumn);
            if (metricAlertLineProperty != null) {
                propriedadeAplicar = metricAlertLineProperty;
                this.currentLineMetricAlert = metricAlertLineProperty;
            }

            metricLinePrinter.printMetricValues(dimensionLines, this.previousLineDimension, dimensionNullColumn,
                    propriedadeAplicar, this.printer, cube,
                    metricLinePrinter instanceof PrintMetricLineMediaPartialLines ? CalculationSummaryType.MEDIA : CalculationSummaryType.NORMAL);
        }
        printMetricColumnTotalValues(dimensionLines, metricCellAlerts,
                metricLinePrinter instanceof PrintMetricLineMediaPartialLines ? CalculationSummaryType.MEDIA : CalculationSummaryType.NORMAL);
        if (!this.metricTotalColumnGeneralSum.isEmpty()) {
            this.printMetricColumnTotalGeneralValues(dimensionLines, metricCellAlerts,
                    metricLinePrinter instanceof PrintMetricLineMediaPartialLines ? CalculationSummaryType.MEDIA : CalculationSummaryType.NORMAL);
        }
        if ("semFuncao".equalsIgnoreCase(metricLineSearchFunction)) {
            this.previousLineDimension = dimensionLines;
        }
    }

    private void printMetricColumnTotalValues(Dimension dimensionLine, List<String> currentAlertFunctions, String lineType) {
        String propriedadeCelula = CellProperty.CELL_PROPERTY_TOTAL_GENERAL;
        if (this.currentLineMetricAlert != null) {
            propriedadeCelula = this.currentLineMetricAlert;
        }

        PrintMetricLineTotalColumnLines sumPrinter = new PrintMetricLineTotalColumnLines(
                this.metricTotalColumnSum, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricMetaData.ACCUMULATED_VALUE_AH, currentAlertFunctions);
        sumPrinter.printMetricValues(dimensionLine, null, dimensionNullColumn, propriedadeCelula, this.printer, cube, lineType);

        PrintMetricLineTotalColumnLines mediaPrinter = new PrintMetricLineTotalColumnLines(
                this.metricTotalColumnMedia, CalculoSumarizacaoTipoMediaColuna.getInstance(), MetricMetaData.MEDIA_AH, currentAlertFunctions);
        mediaPrinter.printMetricValues(dimensionLine, null, dimensionNullColumn, propriedadeCelula, this.printer, cube, lineType);
    }

    private void printMetricColumnTotalGeneralValues(Dimension dimensionLine, List<String> currentAlertFunctions, String lineType) {
        PrintMetricLineTotalGeneralColumnLines printer = new PrintMetricLineTotalGeneralColumnLines(this.metricTotalColumnGeneralSum, currentAlertFunctions);
        String propriedadeCelula = CellProperty.CELL_PROPERTY_TOTAL_GENERAL;
        if (this.currentLineMetricAlert != null) {
            propriedadeCelula = this.currentLineMetricAlert;
        }
        printer.printMetricValues(dimensionLine, null, dimensionNullColumn, propriedadeCelula, this.printer, cube, lineType);
    }

    private int getLowerMetricTotalPartialColumns(Dimension dimension, int metricTotalCount, int metricWithoutAHCount) {
        int result = 0;

        if (!dimension.getMetaData().isLast()) {
            if (dimension.getMetaData().isTotalPartial()) {
                result += dimension.isFirstDimensionColumnSameLevel() ? metricWithoutAHCount : metricTotalCount;
            }

            for (Dimension chieldDimension : dimension.getDimensionsBelow().values()) {
                result += getLowerMetricTotalPartialColumns(chieldDimension, metricTotalCount, metricWithoutAHCount);
            }
        }

        return result;
    }

    public void printDimensionColumn(Dimension dim, String previousLevelCellProperty, int dimensionIndex) {
        int metricsToRemoveCount = 0;
        boolean isFirstDimension = dim.isFirstDimensionColumnSameLevel();

        if (isFirstDimension) {
            metricsToRemoveCount = this.metricsAH.size();
        }

        int colspan = calculateColspan(dim, metricsToRemoveCount);
        this.printer.printColumnValue(previousLevelCellProperty, colspan, 1, dim.getVisualizationValue(), dim.getMetaData());
    }

    private int calculateColspan(Dimension dim, int metricsToRemoveCount) {
        int colspan = (this.metricsAmount * dim.getTotalSize()) - metricsToRemoveCount;
        colspan += this.getLowerMetricTotalPartialColumns(dim, this.metricsAmount, this.metricsAmount - metricsToRemoveCount);
        return colspan;
    }

    public void printDimensionLine(Dimension dim, String previousLevelCellProperty, int sequence) {
        DimensionLine dimensionLine = (DimensionLine) dim;
        int rowspan = calculateRowspan(dimensionLine);
        int colspan = dimensionLine.getColspanLinePrint();
        String rankingSequence = getRankingSequence(dimensionLine);

        if (dim.getMetaData().hasSequenceFields()) {
            this.printer.printSequenceField(dim.getMetaData(), rankingSequence, 1, rowspan);
        }

        this.printer.printDimensionLineValue(previousLevelCellProperty, colspan, rowspan, dimensionLine.getVisualizationValue(), dimensionLine.getMetaData());
    }

    private int calculateRowspan(DimensionLine dimensionLine) {
        return dimensionLine.getTotalSize() + dimensionLine.countPartialAggregatesInHierarchy();
    }

    private String getRankingSequence(DimensionLine dimensionLine) {
        return Optional.ofNullable(dimensionLine.getRankingSequence()).map(String::valueOf).orElse(this.printer.getEmptyValue());
    }
}