package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.data.api.data.consult.CachedResults;
import com.msoft.mbi.data.api.data.consult.ConsultResult;
import com.msoft.mbi.data.api.data.consult.ConsultResultFactory;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.DateException;
import com.msoft.mbi.data.api.data.filters.ConditionalExpression;
import com.msoft.mbi.data.api.data.filters.FilterAccumulated;
import com.msoft.mbi.data.api.data.filters.FilterSequence;
import com.msoft.mbi.data.api.data.htmlbuilder.*;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.IntStream;

@Log4j2
@Getter
@Setter
@SuppressWarnings("unused")
public class Dimension {


    private List<Dimension> bottomDimensions;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Dimension> columnDimension;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Dimension> lineDimension;
    @Setter(AccessLevel.NONE)
    private ConsultResult value;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int index = 1;

    private Dimension parentDimension;
    @Setter(AccessLevel.NONE)
    private Object[][] results;
    @Setter(AccessLevel.NONE)
    private double totalLine;
    private double[] accumulatedLine;
    @Getter(AccessLevel.NONE)
    private Indicator indicator;
    private int counter;
    private boolean isCounterEnabled = false;
    private int columnLineAmount;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public double[] lineExpression;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private PartialTotalizations partialTotalizations;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public HashMap<String, Double> totalLines = null;
    @Setter(AccessLevel.NONE)
    private HashMap<Field, Double> horizontalParticipation = null;
    private boolean mountTableWithoutLink = false;
    private boolean filterBySequence = false;
    private boolean filterByAccumulated = false;
    private double lineSum;
    @Setter(AccessLevel.NONE)
    private boolean alertLineStyle = false;
    @Setter(AccessLevel.NONE)
    private Object lineAppliedStyle = null;
    private double parentValue = 0;
    private boolean parentValueFound = false;

    public Dimension(ConsultResult valor, int size, List<Dimension> line, List<Dimension> column) {
        this.value = valor;
        this.bottomDimensions = new ArrayList<>();
        this.results = new Object[size + 1][];
        this.lineDimension = line;
        this.columnDimension = column;
    }

    public int getLowerDimensionsHeight() {
        int result = 0;
        List<Dimension> bottomDimensions = this.getBottomDimensions();

        if (!bottomDimensions.isEmpty()) {
            if (bottomDimensions.get(0) != null) {
                if (bottomDimensions.get(0).getValue().getField().getDefaultField().equals("S")) {
                    result = 1;
                }
                result += bottomDimensions.get(0).getLowerDimensionsHeight();
            }
        }
        return result;
    }

    public int getLowerDimensionsCount(boolean addToTotal) {
        List<Dimension> nonNullDimensions = this.bottomDimensions.stream()
                .limit(this.bottomDimensions.indexOf(null))
                .toList();

        long dimensionsCount = nonNullDimensions.size();
        long accFilterCount = nonNullDimensions.stream()
                .filter(dimension -> dimension.filterByAccumulated)
                .count();

        if (accFilterCount == dimensionsCount) {
            this.filterByAccumulated = true;
        }

        long count = nonNullDimensions.stream()
                .filter(dimension -> !dimension.filterByAccumulated)
                .mapToLong(dimension -> dimension.getLowerDimensionsCount(addToTotal))
                .map(value -> value > 1 ? value : 1)
                .sum();

        if (addToTotal && this.getValue().getField().isPartialTotalization() &&
                this.getValue().getField().getDefaultField().equals("S") && this.hasLowerDimensions()) {
            count++;
        }

        return (int) count;
    }

    public void updateAccFilter() {
        if (this.hasLowerDimensions()) {
            this.bottomDimensions.forEach(Dimension::updateAccFilter);
        }

        // TODO check if dim can be null
        long dimensionsCount = this.bottomDimensions.size();

        long accFilterCount = this.bottomDimensions.stream()
                .filter(Dimension::isFilterByAccumulated)
                .count();

        if (accFilterCount == dimensionsCount && dimensionsCount > 0) {
            this.filterByAccumulated = true;
        }
    }

    public boolean hasLowerVisibleDimensions() {
        return this.bottomDimensions.stream()
                // .filter(Objects::nonNull) TODO check if dim can be null
                .anyMatch(dimension -> dimension.getValue().getField().getDefaultField().equals("S") || dimension.hasLowerVisibleDimensions());
    }

    public boolean hasMoreLowerDimensions() {
        return this.bottomDimensions.stream()
                // .filter(Objects::nonNull) TODO check if dim can be null
                .anyMatch(dimension -> !dimension.getValue().getField().getDefaultField().equals("N") || dimension.hasLowerVisibleDimensions());
    }

    public boolean hasUpperVisibleDimensions() {
        return Optional.ofNullable(this.parentDimension)
                .map(parent -> "S".equals(parent.getValue().getField().getDefaultField()) || parent.hasUpperVisibleDimensions())
                .orElse(false);
    }

    public boolean hasLowerDimensions() {
        return this.bottomDimensions.stream().anyMatch(Objects::nonNull);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ").append(this.value.getValor(0).toString().trim());

        this.bottomDimensions.stream()
                .takeWhile(Objects::nonNull)
                .forEach(biDimension -> sb.append(" ").append(biDimension));

        sb.append("\n");
        return sb.toString();
    }

    public double lineCalculation(Field metric, int position) throws BIException {
        OptionalInt optionalIndex = IntStream.range(2, this.results[0].length)
                .filter(i -> this.results[0][i] == metric)
                .findFirst();

        if (optionalIndex.isPresent()) {
            return this.lineCalculation(optionalIndex.getAsInt(), position);
        } else {
            return 0d;
        }
    }


    private double lineCalculation(int index, int position) throws BIException {
        Field field = (Field) this.results[0][index];

        if (this.accumulatedLine == null)
            this.accumulatedLine = new double[this.results[0].length];
        if (this.lineExpression == null) {
            this.lineExpression = new double[this.accumulatedLine.length];
        }

        double valAux = 0;

        if (this.hasLowerDimensions()) {
            double total = 0d;
            double valor;
            for (Dimension biDimension : this.bottomDimensions) {
                if (biDimension != null) {
                    biDimension.setIndicator(this.indicator);
                    valor = biDimension.lineCalculation(index, index);
                    this.accumulatedLine[index] += valor;
                    total += valor;

                    if (field.isSumLine()) {
                        this.totalLine += valor;
                    }
                    if (field.isHorizontalParticipation() || field.isHorizontalParticipationAccumulated() || field.getAccumulatedOrder() != 0) {
                        valAux += valor;
                    }
                } else {
                    break;
                }
            }

            if (field.isHorizontalParticipation() || field.isHorizontalParticipationAccumulated() || field.getAccumulatedOrder() != 0) {
                this.getTotalLines().putIfAbsent(String.valueOf(field.getFieldId()), valAux);
            }

            return total;
        } else {
            for (int i = 1; i < this.results.length; i++) {
                if (this.results[i][index] != null) {
                    this.accumulatedLine[index] += (Double) this.results[i][index];
                }
            }
            if (field.isSumLine()) {
                this.totalLine += this.accumulatedLine[index];
            }

            if (field.isHorizontalParticipation() || field.isHorizontalParticipationAccumulated() || field.getAccumulatedOrder() != 0) {
                valAux = this.accumulatedLine[index];
                double finalValAux1 = valAux;
                this.getTotalLines().putIfAbsent(String.valueOf(field.getFieldId()), finalValAux1);
            }
        }

        return this.accumulatedLine[index];
    }

    private boolean hasPartialTotal() {
        return this.value.getField().isPartialTotalization() && this.hasLowerDimensions();
    }

    private void setLineColor(HTMLLine htmlLine, HTMLTable table) {
        if (table.getLineIndex(htmlLine) % 2 == 1) {
            htmlLine.setBackGroundColor("#FFFFFF");
        } else {
            htmlLine.setBackGroundColor("#D7E3F7");
        }
    }

    private void walk(Dimension dimension, HTMLTable table, List<Field> metrics, boolean isColumn, Object[][] resultObject, int dimensionLineIndex, int rootDimensionIndex) throws BIException, DateException {
        Object[][] result = dimension.consult(resultObject);
        if (result.length > 1 && dimension.hasLowerDimensions()) {
            List<Dimension> lowerDimensions = dimension.getBottomDimensions();
            for (int a = 0; a < lowerDimensions.size(); a++) {
                if (lowerDimensions.get(a) != null) {
                    if (dimensionLineIndex != 0 && rootDimensionIndex == 0) {
                        rootDimensionIndex = -1;
                    }
                    walk(lowerDimensions.get(a), table, metrics, isColumn, result, a, rootDimensionIndex);
                } else {
                    break;
                }
            }
        } else if (!this.filterByAccumulated) {
            HTMLLine htmlLine = table.getCurrentLine();

            this.setLineColor(htmlLine, table);

            for (Field metric : metrics) {
                if (metric != null && !metric.getName().isEmpty()) {
                    if (isColumn) {
                        for (int x = 0; x < metrics.size() - metrics.indexOf(metric) - 1; x++) {
                            htmlLine = table.getPreviousLine(htmlLine);
                        }
                    }
                    this.processMetric(htmlLine, table, metric, dimension, result, dimensionLineIndex, rootDimensionIndex);
                }
            }
        }
    }

    private void processMetric(HTMLLine htmlLine, HTMLTable table, Field metric, Dimension dimension, Object[][] result, int dimensionLineIndex, int rootDimensionIndex) throws BIException, DateException {

        if (result.length > 1) {
            this.processMultipleLines(htmlLine, metric, dimension, result, dimensionLineIndex, rootDimensionIndex);
        } else {
            this.processSingleLine(htmlLine, metric, dimension, dimensionLineIndex, rootDimensionIndex);
        }
    }

    private void processMultipleLines(HTMLLine htmlLine, Field metric, Dimension dimension, Object[][] result, int dimensionLineIndex, int rootDimensionIndex) throws BIException, DateException {
        for (int j = 0; j < result[0].length; j++) {
            Field field = (Field) result[0][j];
            double valueOne = (Double) result[1][j];

            if (field == null || !field.equals(metric) || field.getDefaultField().equals("T")) {
                continue;
            }

            htmlLine.addCell(new HTMLCell());
            htmlLine.getCurrentCell().setNowrap(true);
            htmlLine.getCurrentCell().setAlignment(field.getColumnAlignment());
            HTMLStyle style = MultidimensionalStyles.getInstancia().getEstilos().get(MultidimensionalStyles.ESTILO_VAL_METRICA_COLUNA);

            int decimalPositions = field.getNumDecimalPositions();
            htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(valueOne, decimalPositions));

            boolean applyAlertValue = this.indicator.getColorAlerts().searchAlertValueApply(valueOne, htmlLine.getCurrentCell(), htmlLine, field, ColorAlert.SEM_FUNCAO, field.getNumDecimalPositions(), this, true);
            if (applyAlertValue) {
                this.setAlertLineStyle(applyAlertValue);
            }
            applyAlertValue = this.indicator.getColorAlerts().searchOtherFieldAlertApply(valueOne, field, ColorAlert.SEM_FUNCAO, result, htmlLine.getCurrentCell(), htmlLine, field.getNumDecimalPositions(), this, dimension, null, 0, true);
            if (applyAlertValue) {
                this.setAlertLineStyle(applyAlertValue);
            }

            if (this.alertLineStyle) {
                htmlLine.setStyle(lineAppliedStyle);
            } else if (!htmlLine.getCurrentCell().isAppliedAlert()) {
                htmlLine.getCurrentCell().setStyle(style);
            }
            Double sum = 0d;
            if (field.isAccumulatedParticipation() || field.isAccumulatedValue()) {
                sum = this.retrieveAccSum(result, j);
            }

            if (field.isTotalizingField()) {
                this.handleTotalizedField(htmlLine, result, field, sum, style, valueOne, (Double) result[1][j + 1]);
            }

            if (field.isAccumulatedValue()) {
                this.handleAccumulatedField(htmlLine, field, result, sum, style);
            }

            if (field.isHorizontalAnalysis() && !(dimensionLineIndex == 0 && rootDimensionIndex == 0)) {
                this.handleHorizontalAnalysis(htmlLine, field, result, style, valueOne, j);
            }

            if (field.isHorizontalParticipation()) {
                this.handleHorizontalParticipation(htmlLine, field, result, style, valueOne);
            }

            if (field.isHorizontalParticipationAccumulated()) {
                this.handleHorizontalParticipationAccumulated(htmlLine, field, result, style, valueOne);
            }
        }
    }

    private void handleHorizontalParticipationAccumulated(HTMLLine htmlLine, Field field, Object[][] result, HTMLStyle style, double valueOne) throws BIException, DateException {
        htmlLine.addCell(new HTMLCell());
        htmlLine.getCurrentCell().setNowrap(true);
        htmlLine.getCurrentCell().setAlignment(field.getColumnAlignment());

        if (horizontalParticipation == null) {
            horizontalParticipation = new HashMap<>();
        }
        if (field.isMetric()) {
            double valAux;
            double ini = valueOne;
            double total = this.getTotalLines().get(String.valueOf(field.getFieldId()));

            if (ini != 0) {
                if (horizontalParticipation.get(field) != null) {
                    valAux = horizontalParticipation.get(field);
                    ini = ini / total + valAux;
                    horizontalParticipation.remove(field);
                } else {
                    ini = ini / total;
                }
                horizontalParticipation.put(field, ini);
                htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(ini, 2));

                boolean applyAlertValue = this.indicator.getColorAlerts().searchAlertValueApply(ini * 100, htmlLine.getCurrentCell(), htmlLine, field, ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL, 2, this, true);
                if (applyAlertValue) {
                    this.setAlertLineStyle(applyAlertValue);
                }

                applyAlertValue = this.indicator.getColorAlerts().searchOtherFieldAlertApply(ini * 100, field, ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL, result, htmlLine.getCurrentCell(), htmlLine, 2, this, null, null, 0, true);
                if (applyAlertValue) {
                    this.setAlertLineStyle(applyAlertValue);
                }
            } else {
                htmlLine.getCurrentCell().setContent("0");
            }
        }
        if (this.alertLineStyle) {
            htmlLine.setStyle(lineAppliedStyle);
        } else if (!htmlLine.getCurrentCell().isAppliedAlert()) {
            htmlLine.getCurrentCell().setStyle(style);
        }
    }

    private void handleHorizontalParticipation(HTMLLine htmlLine, Field field, Object[][] result, HTMLStyle style, double valueOne) throws BIException, DateException {
        htmlLine.addCell(new HTMLCell());
        htmlLine.getCurrentCell().setNowrap(true);
        htmlLine.getCurrentCell().setAlignment(field.getColumnAlignment());

        if (!field.isHorizontalParticipation() || !field.isMetric()) {
            return;
        }

        double total = this.getTotalLines().get(String.valueOf(field.getFieldId()));

        if (total != 0) {
            boolean applyAlertValue = this.indicator.getColorAlerts().searchAlertValueApply((valueOne / total) * 100, htmlLine.getCurrentCell(), htmlLine, field, ColorAlert.PARTICIPACAO_HORIZONTAL, 2, this, true);
            if (applyAlertValue) {
                this.setAlertLineStyle(applyAlertValue);
            }

            applyAlertValue = this.indicator.getColorAlerts().searchOtherFieldAlertApply((valueOne / total) * 100, field, ColorAlert.PARTICIPACAO_HORIZONTAL, result, htmlLine.getCurrentCell(), htmlLine, 2, this, null, null, 0, true);
            if (applyAlertValue) {
                this.setAlertLineStyle(applyAlertValue);
            }
        }

        if (valueOne != 0) {
            htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText((valueOne / total), 2));
        } else {
            htmlLine.getCurrentCell().setContent("0");
        }

        if (this.alertLineStyle) {
            htmlLine.setStyle(lineAppliedStyle);
        } else if (!htmlLine.getCurrentCell().isAppliedAlert()) {
            htmlLine.getCurrentCell().setStyle(style);
        }
    }

    private void handleHorizontalAnalysis(HTMLLine htmlLine, Field field, Object[][] result, HTMLStyle style, double valueOne, int resultIndex) throws BIException, DateException {
        htmlLine.addCell(new HTMLCell());
        htmlLine.getCurrentCell().setNowrap(true);
        htmlLine.getCurrentCell().setAlignment(field.getColumnAlignment());

        for (int ii = resultIndex + 1; result[0][ii] != null; ii++) {
            if (((Field) result[0][ii]).isHorizontalAnalysis()) {
                if (result[1][ii] != null) {
                    double ini, fin, res;
                    fin = valueOne;
                    ini = (Double) result[1][ii];
                    if (ini != fin) {
                        if (ini != 0) {
                            res = (ini - fin) / ini;
                            htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText((-1 * res), 2));
                            boolean applyAlertValue = this.indicator.getColorAlerts().searchAlertValueApply(-1 * res * 100, htmlLine.getCurrentCell(), htmlLine, field, ColorAlert.ANALISE_HORIZONTAL, field.getNumDecimalPositions(), this, true);
                            if (applyAlertValue) {
                                this.setAlertLineStyle(applyAlertValue);
                            }

                            applyAlertValue = this.indicator.getColorAlerts().searchOtherFieldAlertApply(-1 * res * 100, field, ColorAlert.ANALISE_HORIZONTAL, result, htmlLine.getCurrentCell(), htmlLine, 2, this, null, null, 0, true);
                            if (applyAlertValue) {
                                this.setAlertLineStyle(applyAlertValue);
                            }
                        } else {
                            if (fin > 0) {
                                htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(1, 2));
                            } else if (fin < 0) {
                                htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(-1, 2));
                            }
                        }
                    } else {
                        htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(0, 2));
                    }
                } else {
                    htmlLine.getCurrentCell().setContent("-");
                }
                break;
            }
        }
        if (this.alertLineStyle) {
            htmlLine.setStyle(lineAppliedStyle);
        } else if (!htmlLine.getCurrentCell().isAppliedAlert()) {
            htmlLine.getCurrentCell().setStyle(style);
        }
    }

    private void handleAccumulatedField(HTMLLine htmlLine, Field field, Object[][] result, double sum, HTMLStyle style) throws BIException, DateException {

        int decimalPositions = field.getNumDecimalPositions();

        htmlLine.addCell(new HTMLCell());
        htmlLine.getCurrentCell().setNowrap(true);
        htmlLine.getCurrentCell().setAlignment(field.getColumnAlignment());

        htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(sum, decimalPositions));
        boolean applyAlertValue = this.indicator.getColorAlerts().searchAlertValueApply(sum, htmlLine.getCurrentCell(), htmlLine, field, ColorAlert.ACUMULADO_VERTICAL, field.getNumDecimalPositions(), this, true);
        if (applyAlertValue) {
            this.setAlertLineStyle(applyAlertValue);
        }

        applyAlertValue = this.indicator.getColorAlerts().searchOtherFieldAlertApply(sum, field, ColorAlert.ACUMULADO_VERTICAL, result, htmlLine.getCurrentCell(), htmlLine, field.getNumDecimalPositions(), this, null, null, 0, true);
        if (applyAlertValue) {
            this.setAlertLineStyle(applyAlertValue);
        }

        if (this.alertLineStyle) {
            htmlLine.setStyle(lineAppliedStyle);
        } else if (!htmlLine.getCurrentCell().isAppliedAlert()) {
            htmlLine.getCurrentCell().setStyle(style);
        }
    }

    private void handleTotalizedField(HTMLLine htmlLine, Object[][] result, Field field, double sum, HTMLStyle style, double valueOne, double valueTwo) throws BIException, DateException {
        if (field.isVerticalAnalysis()) {
            htmlLine.addCell(new HTMLCell());
            htmlLine.getCurrentCell().setNowrap(true);
            if (valueTwo == 0) {
                htmlLine.getCurrentCell().setContent("-");
            } else {
                htmlLine.getCurrentCell().setAlignment(field.getColumnAlignment());
                double percentage = valueOne / valueTwo;
                htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(percentage, 2));

                boolean applyAlertValue = this.indicator.getColorAlerts().searchAlertValueApply(percentage * 100, htmlLine.getCurrentCell(), htmlLine, field, ColorAlert.ANALISE_VERTICAL, field.getNumDecimalPositions(), this, true);
                if (applyAlertValue) {
                    this.setAlertLineStyle(applyAlertValue);
                }

                applyAlertValue = this.indicator.getColorAlerts().searchOtherFieldAlertApply(percentage * 100, field, ColorAlert.ANALISE_VERTICAL, result, htmlLine.getCurrentCell(), htmlLine, 2, this, null, null, 0, true);
                if (applyAlertValue) {
                    this.setAlertLineStyle(applyAlertValue);
                }

                if (this.alertLineStyle) {
                    htmlLine.setStyle(lineAppliedStyle);
                } else if (!htmlLine.getCurrentCell().isAppliedAlert()) {
                    htmlLine.getCurrentCell().setStyle(style);
                }

            }
        }
        if (field.isAccumulatedParticipation()) {
            htmlLine.addCell(new HTMLCell());
            htmlLine.getCurrentCell().setNowrap(true);
            if (valueTwo == 0) {
                htmlLine.getCurrentCell().setContent("-");
            } else {
                htmlLine.getCurrentCell().setAlignment(field.getColumnAlignment());

                htmlLine.getCurrentCell().setContent(BIUtil.formatDoubleToText(sum / valueTwo, 2));

                boolean applyAlertValue = this.indicator.getColorAlerts().searchAlertValueApply(sum / valueTwo * 100, htmlLine.getCurrentCell(), htmlLine, field, ColorAlert.PARTICIPACAO_ACUMULADA, 2, this, true);
                if (applyAlertValue) {
                    this.setAlertLineStyle(applyAlertValue);
                }
                applyAlertValue = this.indicator.getColorAlerts().searchOtherFieldAlertApply((sum / valueTwo) * 100, field, ColorAlert.PARTICIPACAO_ACUMULADA, result, htmlLine.getCurrentCell(), htmlLine, 2, this, null, null, 0, true);
                if (applyAlertValue) {
                    this.setAlertLineStyle(applyAlertValue);
                }

                if (this.alertLineStyle) {
                    htmlLine.setStyle(lineAppliedStyle);
                } else if (!htmlLine.getCurrentCell().isAppliedAlert()) {
                    htmlLine.getCurrentCell().setStyle(style);
                }
            }
        }
    }

    private void processSingleLine(HTMLLine htmlLine, Field metric, Dimension dimension, int dimensionLineIndex, int rootDimensionIndex) {
        int sheets = this.contFolhas(dimension);
        if (sheets == 0)
            sheets = 1;

        HTMLStyle htmlStyle = MultidimensionalStyles.getInstancia().getEstilos().get(MultidimensionalStyles.ESTILO_VAL_METRICA_COLUNA);

        for (int i = 0; i < sheets; i++) {
            if (!metric.getDefaultField().equals("T")) {
                addCellToLine(htmlLine, metric, htmlStyle);
                if (metric.isTotalizingField()) {
                    if (metric.isVerticalAnalysis() || metric.isAccumulatedParticipation()) {
                        addCellToLine(htmlLine, metric, htmlStyle);
                    }
                }
                if (metric.isAccumulatedValue() ||
                        (metric.isHorizontalAnalysis() && !(i == 0 && dimensionLineIndex == 0 && rootDimensionIndex == 0)) ||
                        metric.isHorizontalParticipation() ||
                        metric.isHorizontalParticipationAccumulated()) {
                    addCellToLine(htmlLine, metric, htmlStyle);
                }
            }
        }
    }

    private void addCellToLine(HTMLLine htmlLine, Field metric, HTMLStyle htmlStyle) {
        htmlLine.addCell(new HTMLCell());
        htmlLine.getCurrentCell().setNowrap(true);
        htmlLine.getCurrentCell().setAlignment(metric.getColumnAlignment());
        htmlLine.getCurrentCell().setStyle(htmlStyle);
        htmlLine.getCurrentCell().setContent("-");
    }

    private Double retrieveAccSum(Object[][] result, int currentFieldPosition) {
        for (int i = currentFieldPosition + 1; i < result[1].length; i++) {
            if (result[0][i] != null && (((Field) result[0][i]).isAccumulatedValue() || ((Field) result[0][i]).isAccumulatedParticipation()) && ((Field) result[0][i]).getName().equalsIgnoreCase("")) {
                return (Double) result[1][i];
            }
        }
        return 0D;
    }

    public Double updateTest(Field metric, Object[] result, Double value, int index) {
        Field campo;
        int i;
        int posicaoLinha = 0;
        for (i = 0; i < this.results[0].length; i++) {
            campo = (Field) this.results[0][i];
            if (Constants.DIMENSION.equals(campo.getFieldType())) {
                if (Constants.LINE == campo.getDisplayLocation()) {
                    posicaoLinha = i;
                    break;
                }
            }
        }
        List<Dimension> dimensoes = this.lineDimension;
        Dimension dimensao = null;
        for (int ii = 0; ii < this.lineDimension.size(); ii++) {
            if (dimensoes.get(ii) != null) {
                if (dimensoes.get(ii).getValue().getValor(0).equals(result[posicaoLinha])) {
                    dimensao = dimensoes.get(ii);
                    dimensoes = dimensao.bottomDimensions;
                    ii = -1;
                    posicaoLinha++;
                }
            } else {
                break;
            }
        }
        for (i = index + 1; i < dimensao.results[0].length; i++) {
            campo = (Field) this.results[0][i];
            if (campo.getFieldType() != null) {
                break;
            } else if (campo.isAccumulatedValue() || campo.isAccumulatedParticipation()) {
                Double atual = (Double) dimensao.results[1][i];
                if (atual == null) {
                    atual = 0d;
                }
                atual = atual + value;
                return atual;
            }
        }
        return 0d;
    }

    private int contFolhas(Dimension raiz) {
        int cont = 0;
        if (raiz.hasLowerDimensions()) {
            List<Dimension> dimensoesAbaixo = raiz.getBottomDimensions();
            if (dimensoesAbaixo.get(0).hasLowerDimensions()) {
                for (Dimension biDimension : dimensoesAbaixo) {
                    if (biDimension != null) {
                        cont += contFolhas(biDimension);
                    }
                }
            } else {
                cont = raiz.getLowerDimensionsCount(false);
            }
        }
        return cont;
    }

    public Object[][] consult(Object[][] results) {
        int column = -1;
        Object[][] objFields = new Object[results.length][results[0].length];
        objFields[0] = results[0];
        for (int i = 0; i < results[0].length; i++) {
            if (results[0][i] != null) {
                if (((Field) results[0][i]).getFieldId() == this.value.getField().getFieldId()) {
                    column = i;
                    break;
                }
            }
        }
        int cont = 1;
        if (column != -1) {
            for (int i = 1; i < results.length; i++) {
                try {
                    if (this.value.getValor(0) != null && this.value.getValor(0).equals(results[i][column])) {

                        Dimension dimenAux = this;
                        int dimensionCount = 1;
                        boolean sair = false;
                        while (dimenAux.getParentDimension() != null) {
                            dimenAux = dimenAux.getParentDimension();

                            if (!dimenAux.value.getValor(0).equals(results[i][column - dimensionCount])) {
                                sair = true;
                                break;
                            }
                            dimensionCount++;
                        }
                        if (sair)
                            continue;

                        objFields[cont] = results[i];
                        cont++;
                    }
                } catch (NullPointerException e) {
                    log.error("Erro ao consultar a dimensão: " + this.value.getValor(0));
                }
            }
        }

        Object[][] objAux2 = new Object[cont][objFields[0].length];
        cont = 0;
        boolean temValor;
        for (Object[] objField : objFields) {
            if (objField != null) {
                temValor = false;

                for (Object o : objField) {
                    if (o != null) {
                        temValor = true;
                        break;
                    }
                }

                if (temValor) {
                    objAux2[cont] = objField;
                    cont++;
                }
            }
        }
        int posicao = 0;
        for (int i = 2; i < objAux2[0].length; i++) {
            if (objAux2[0][i] != null && ((Field) objAux2[0][i]).isHorizontalAnalysis()) {
                if (((Field) objAux2[0][i]).getTitle().contains(" AH%")) {
                    for (int ii = 2; ii < objAux2.length; ii++) {
                        if ("D".equals((objAux2[0][i]))) {
                            objAux2[ii][i] = objAux2[ii - 1][posicao];
                        } else {
                            objAux2[ii][i] = objAux2[1][posicao];
                        }
                    }
                } else {
                    posicao = i;
                }
            }
        }
        return objAux2;
    }

    public void toHTMLLine(HTMLTable table, HTMLLine line, HTMLLine bottonLine, Field[] lineMetrics, List<Field> columnFields, int colspanHeader, int lineDimensionIndex, int rooDimensionIndex) throws BIException, DateException, CloneNotSupportedException {
        boolean todosNull = true;
        int counter;
        int dimensionIndex = 0;

        MultidimensionalStyles multidimensionalStyles = MultidimensionalStyles.getInstancia();

        HTMLStyle htmlStyleOne = multidimensionalStyles.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_DIMENSAO_LINHA);

        HTMLStyle htmlStyleTwo = multidimensionalStyles.getEstilos().get(MultidimensionalStyles.ESTILO_VAL_DIMENSAO_LINHA);

        for (Dimension bottomDimension : this.bottomDimensions) {
            if (bottomDimension != null) {
                if (!bottomDimension.getValue().getField().getDefaultField().equals("T")) {
                    bottomDimension.setMountTableWithoutLink(this.mountTableWithoutLink);
                    if (line.getCells().isEmpty()) {
                        line.addCell(new HTMLCell());
                        line.getCurrentCell().setAlignment(CellProperty.ALIGNMENT_RIGHT);

                        line.getCurrentCell().setTHCell(true);

                        line.getCurrentCell().setStyle(htmlStyleOne);
                        line.getCurrentCell().setColspan(colspanHeader);
                        LinkHTML linkField = new LinkHTML("javascript:alteraField('" + bottomDimension.getValue().getField().getFieldId() + "');", bottomDimension.getValue().getField().getTitle(), "textWhiteOff");
                        if (!this.isMountTableWithoutLink()) {
                            line.getCurrentCell().setContent("<b>" + linkField + "</b>");
                        } else {
                            line.getCurrentCell().setContent("<b>" + bottomDimension.getValue().getField().getTitle() + "</b>");
                        }
                        line.getCurrentCell().setAppliedAlert(true);
                    }

                    todosNull = false;
                    line.addCell(new HTMLCell());
                    if (bottomDimension.hasLowerDimensions()) {
                        int colspan = bottomDimension.getLowerDimensionsCount(false);
                        if (lineMetrics != null && lineMetrics.length > 0) {

                            counter = (int) Arrays.stream(lineMetrics)
                                    .filter(field -> field != null
                                            && !field.getName().isEmpty()
                                            && !field.getTitle().equals("total")
                                            && !field.getDefaultField().equals("T"))
                                    .count();

                            colspan *= counter;
                        }
                        line.getCurrentCell().setColspan(colspan);
                    } else {
                        if (lineMetrics != null) {
                            counter = (int) Arrays.stream(lineMetrics)
                                    .filter(field -> field != null
                                            && !field.getName().isEmpty()
                                            && !field.getTitle().equals("total")
                                            && !field.getDefaultField().equals("T"))
                                    .count();
                            line.getCurrentCell().setColspan(counter);
                        }
                    }

                    int metricAHCount = this.getMetricLinesWithAH(lineMetrics);
                    if (rooDimensionIndex == 0 && dimensionIndex == 0 && metricAHCount > 0) {
                        line.getCurrentCell().setColspan(line.getCurrentCell().getColspan() - metricAHCount);
                    }

                    line.getCurrentCell().setAlignment(CellProperty.ALIGNMENT_CENTER);
                    line.getCurrentCell().setContent(bottomDimension.getValue().getFormattedValue(0));
                    boolean appliedLineAlert = this.indicator.getColorAlerts().searchAlertValueApply(bottomDimension.getValue().getFormattedValue(0), line.getCurrentCell(), line, bottomDimension.getValue().getField(), ColorAlert.SEM_FUNCAO, bottomDimension.getValue().getField().getNumDecimalPositions(), this, true);
                    if (appliedLineAlert) {
                        line.setAppliedAlert(true);
                    }

                    if (!line.isAppliedAlert() && !line.getCurrentCell().isAppliedAlert()) {
                        line.getCurrentCell().setStyle(htmlStyleTwo);
                    }

                    line.getCurrentCell().setAlignment(bottomDimension.getValue().getField().getColumnAlignment());
                    line.getCurrentCell().setWidth(String.valueOf(bottomDimension.getValue().getField().getColumnWidth()));

                    if (bottonLine == null) {
                        bottonLine = new HTMLLine();
                        table.addLine(bottonLine);
                    }
                    if (dimensionIndex != 0 && rooDimensionIndex == 0) {
                        rooDimensionIndex = -1;
                    }

                    bottomDimension.setIndicator(this.indicator);
                    bottomDimension.setMountTableWithoutLink(this.mountTableWithoutLink);
                    bottomDimension.toHTMLLine(table, bottonLine, table.getNextLine(bottonLine), lineMetrics, columnFields, colspanHeader, dimensionIndex, rooDimensionIndex);
                } else {

                    if (dimensionIndex != 0 && rooDimensionIndex == 0) {
                        rooDimensionIndex = -1;
                    }

                    todosNull = false;
                    bottomDimension.setIndicator(this.indicator);
                    bottomDimension.setMountTableWithoutLink(this.mountTableWithoutLink);
                    bottomDimension.toHTMLLine(table, line, table.getNextLine(line), lineMetrics, columnFields, colspanHeader, dimensionIndex, rooDimensionIndex);
                }
            }
            dimensionIndex++;
        }
        if (todosNull) {
            if (line.getCells().isEmpty()) {
                if (columnFields != null) {
                    HTMLStyle style = multidimensionalStyles.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_DIMENSAO_LINHA);

                    if (this.indicator.isUsesSequence()) {
                        line.addCell(new HTMLCell());
                        line.getCurrentCell().setBackGroundColor("#3377CC");
                        line.getCurrentCell().setBorderColor("#FFFFFF");
                        line.getCurrentCell().setStyle(style);
                        line.getCurrentCell().setAlignment(CellProperty.ALIGNMENT_CENTER);
                        line.getCurrentCell().setContent("Seq");
                    }

                    for (Field field : columnFields) {
                        if (field != null && !field.getDefaultField().equals("T")) {
                            line.addCell(new HTMLCell());
                            line.getCurrentCell().setBackGroundColor("#3377CC");
                            line.getCurrentCell().setBorderColor("#FFFFFF");
                            line.getCurrentCell().setStyle(style);
                            line.getCurrentCell().setAlignment(CellProperty.ALIGNMENT_CENTER);
                            line.getCurrentCell().setTHCell(true);
                            HTMLTable tabelaAux = new HTMLTable();
                            tabelaAux.setWidth("100%");
                            tabelaAux.addLine(new HTMLLine());
                            tabelaAux.getCurrentLine().addCell(new HTMLCell());
                            tabelaAux.getCurrentLine().getCurrentCell().setWidth("100%");
                            HTMLStyle estiloAux = (HTMLStyle) style.clone();
                            tabelaAux.getCurrentLine().getCurrentCell().setStyle(estiloAux);
                            LinkHTML linkField = new LinkHTML("javascript:alteraField('" + field.getFieldId() + "');", field.getTitle(), "textWhiteOff");
                            if (!this.isMountTableWithoutLink()) {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent(linkField);
                            } else {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent(field.getTitle());
                            }

                            tabelaAux.getCurrentLine().addCell(new HTMLCell());

                            boolean temFiltro = false;
                            if (field.indicator.getFilters().getDimensionFilter() != null) {
                                temFiltro = field.indicator.checkFilters(field.indicator.getFilters().getDimensionFilter(), field);
                            }

                            LinkHTML link;
                            HTMLImage imagem;

                            if (temFiltro) {
                                imagem = new HTMLImage("imagens/_filter.gif", "12px", "12px");
                            } else {
                                imagem = new HTMLImage("imagens/_not_filter.png", "16px", "16px");
                            }

                            imagem.setId(field.getNickname() + field.getFieldId());

                            link = new LinkHTML("javascript:filtroHeader(" + field.getFieldId() + ",'" + imagem.getId() + "');", imagem);
                            tabelaAux.getCurrentLine().getCurrentCell().setContent("&nbsp;" + link);

                            tabelaAux.getCurrentLine().addCell(new HTMLCell());

                            link = null;
                            if (field.isNavigable()) {
                                link = new LinkHTML("javascript:browseDown(" + field.getFieldId() + ");", new HTMLImage("imagens/_mais.gif"));
                            } else if (field.isNavigableUpwards()) {
                                link = new LinkHTML("javascript:browseUp(" + field.getFieldId() + ");", new HTMLImage("imagens/_menos.gif"));
                            }
                            if (link != null && !this.isMountTableWithoutLink()) {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent(link);
                            } else {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent("&nbsp;");
                            }
                            line.getCurrentCell().setWidth(String.valueOf(field.getColumnWidth()));
                            line.getCurrentCell().setContent(tabelaAux);
                        }
                    }
                }
            }
            if (lineMetrics != null) {

                HTMLStyle style = multidimensionalStyles.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_METRICA_LINHA);

                for (Field field : lineMetrics) {
                    if (field != null) {
                        if (!field.getDefaultField().equals("T") && !(field.getName().isEmpty() && field.getTitle().equals("total"))) {
                            if (!(rooDimensionIndex == 0 && lineDimensionIndex == 0 && field.isHorizontalAnalysis() && field.getTitle().contains("AH%"))) {
                                line.addCell(new HTMLCell());
                                line.getCurrentCell().setBackGroundColor("#A2C8E8");
                                line.getCurrentCell().setTHCell(true);
                                line.getCurrentCell().setStyle(style);
                                line.getCurrentCell().setWidth(String.valueOf(field.getColumnWidth()));
                                line.getCurrentCell().setAlignment(field.getColumnAlignment());
                                line.getCurrentCell().setNowrap(true);
                                LinkHTML linkField = new LinkHTML("javascript:alteraField('" + field.getFieldId() + "');", field.getTitle(), "blueLinkDrillDown");
                                if (!this.isMountTableWithoutLink() && !field.isChildField()) {
                                    line.getCurrentCell().setContent(linkField);
                                } else {
                                    line.getCurrentCell().setContent(field.getTitle());
                                }
                            }
                        }
                    }
                }
            } else {
                line.addCell(new HTMLCell());
                line.getCurrentCell().setAlignment(CellProperty.ALIGNMENT_CENTER);
                line.getCurrentCell().setBackGroundColor("#A2C8E8");
                line.getCurrentCell().setContent("&nbsp;");
            }
        }
    }

    public void setResults(Object[][] results) {
        this.results[0] = results[0];
        this.results[this.index] = results[1];
        this.index++;
        if (this.parentDimension != null) {
            this.parentDimension.setResults(results);
        }
    }

    public Dimension getLastDimension() {
        Dimension ret = null;
        Dimension aux = this;
        while (aux != null && aux.bottomDimensions != null && !aux.bottomDimensions.isEmpty()) {
            ret = aux;
            aux = aux.bottomDimensions.get(0);
        }
        return ret;
    }

    public void resizeResults() {
        for (Dimension bottonDimension : this.bottomDimensions) {
            if (bottonDimension != null) {
                bottonDimension.resizeResults();
            } else {
                this.resizeResults(this.results);
                break;
            }
        }
    }

    protected void resizeResults(Object[][] vector) {
        List<Object[]> nonNullRows = Arrays.stream(vector)
                .filter(Objects::nonNull)
                .toList();
        this.results = nonNullRows.toArray(new Object[0][]);
    }

    public void walkDimensionsLine(List<Dimension> dimensions, HTMLTable table) {
        dimensions.stream()
                .filter(Objects::nonNull)
                .forEach(dimension -> dimension.walkDimensionsLine(dimension.bottomDimensions, table));
    }

    private boolean writeTotalPartial(HTMLLine htmlLine, int lineIndex, int sequence, List<DimensionTotalized> dimensionsTotal) throws BIException, DateException {
        boolean total = false;
        HashMap<Field, Object> horizontalParticipation = new HashMap<>();
        HashMap<Field, List<Object>> horizontalParticipations = new HashMap<>();

        HTMLCell cell;
        MultidimensionalStyles multidimensionalStyles = MultidimensionalStyles.getInstancia();

        HTMLStyle htmlStyle = multidimensionalStyles.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_METRICA_LINHA);

        if (this.hasLowerDimensions()) {
            cell = new HTMLCell();
            htmlLine.addCell(cell);
            cell.setStyle(htmlStyle);
            cell.setNowrap(true);
            cell.setContent("Total");
            cell.setDimensionColumn(true);
            cell.setAlignment(CellProperty.ALIGNMENT_LEFT);
            cell.setBackGroundColor("#CCCCCC");
            cell.setTHCell(true);
            cell.setColspan(this.getLowerDimensionsHeight());
            for (int i = 0; i < this.columnDimension.size(); i++) {
                if (this.lineDimension.get(i) != null) {
                    total = total || this.writeTotalPartial(this.lineDimension.get(i), this.results, htmlLine, sequence, i, i, horizontalParticipation, dimensionsTotal, horizontalParticipations);
                } else {
                    break;
                }
            }
            Field field;
            for (int ii = 3; ii < this.results[0].length; ii++) {
                field = (Field) this.results[0][ii];
                double aux = 0d;
                if (this.accumulatedLine != null)
                    aux = this.accumulatedLine[ii];

                if (field != null && !field.getDefaultField().equals("T")) {

                    
                    if (field.isApplyTotalizationExpression()) {
                        this.partialTotalizations = this.indicator.getPartialTotalizations();
                        if (this.accumulatedLine != null && this.partialTotalizations != null) {
                            this.accumulatedLine[ii] = this.partialTotalizations.getTotalPartialAccumulated(field, sequence);
                        }
                    }
                    if (!field.getAccumulatedLine().equals("N")) {
                        cell = new HTMLCell();
                        htmlLine.addCell(cell);
                        cell.setNowrap(true);
                        cell.setStyle(htmlStyle);
                        cell.setAlignment(field.getColumnAlignment());
                        int decimalPositions = field.getNumDecimalPositions();

                        if (field.isPartialTotalization()) {
                            this.accumulatedLine[ii] = this.getAccLineUpdated(ii);
                            if ("E".equals(field.getAccumulatedLine())) {
                                this.accumulatedLine[ii] = this.calAccExpression(field, ii);
                            }
                            cell.setContent(BIUtil.formatDoubleToText(this.accumulatedLine[ii], decimalPositions));
                            this.indicator.getColorAlerts().searchAlertValueApply(this.accumulatedLine[ii], cell, htmlLine, field, ColorAlert.TOTALIZACAO_PARCIAL, field.getNumDecimalPositions(), this, true);
                        } else {
                            cell.setContent("-");
                        }
                    }
                    if (field.isSumLine() && this.accumulatedLine != null) {
                        this.totalLine -= aux;
                        this.totalLine += this.accumulatedLine[ii];
                        total = true;
                    }
                }
            }

            for (int ii = 3; ii < this.results[0].length; ii++) {
                field = (Field) this.results[0][ii];
                if (field != null && !field.getDefaultField().equals("T")) {
                    if (field.isMediaLine()) {
                        cell = new HTMLCell();
                        htmlLine.addCell(cell);
                        cell.setNowrap(true);
                        cell.setStyle(htmlStyle);
                        cell.setAlignment(field.getColumnAlignment());
                        int decimalPositions = field.getNumDecimalPositions();
                        if (field.isPartialTotalization()) {
                            if ("E".equals(field.getAccumulatedLine())) {
                                this.accumulatedLine[ii] = this.calAccExpression(field, ii);
                            }
                            if (getColumnLineAmount() > 0) {
                                cell.setContent(BIUtil.formatDoubleToText(this.accumulatedLine[ii] / getColumnLineAmount(), decimalPositions));
                                this.indicator.getColorAlerts().searchAlertValueApply(this.accumulatedLine[ii] / getColumnLineAmount(), cell, htmlLine, field, ColorAlert.TOTALIZACAO_PARCIAL, field.getNumDecimalPositions(), this, true);
                            } else {
                                cell.setContent(BIUtil.formatDoubleToText(this.accumulatedLine[ii], decimalPositions));
                                this.indicator.getColorAlerts().searchAlertValueApply(this.accumulatedLine[ii], cell, htmlLine, field, ColorAlert.TOTALIZACAO_PARCIAL, field.getNumDecimalPositions(), this, true);
                            }
                        } else {
                            cell.setContent("-");
                        }
                    }
                }
            }
            if (total) {
                cell = new HTMLCell();
                htmlLine.addCell(cell);
                cell.setStyle(htmlStyle);
                cell.setNowrap(true);
                cell.setAlignment(CellProperty.ALIGNMENT_RIGHT);
                cell.setContent(BIUtil.formatDoubleToText(this.totalLine, 2));
            }
        }
        return total;
    }

    private boolean writeTotalPartial(Dimension dimension,
                                      Object[][] results, HTMLLine htmlLine, int sequence, int lineDimensionIndex, int rootDimensionIndex,
                                      HashMap<Field, Object> participParcialHorizontal, List<DimensionTotalized> totalizedDimensions,
                                      HashMap<Field, List<Object>> horizontalParticipations) throws BIException, DateException {
        Object[][] values = dimension.consult(results);
        boolean total = false;
        if (dimension.hasLowerDimensions()) {
            for (int i = 0; i < dimension.bottomDimensions.size(); i++) {
                if (dimension.bottomDimensions.get(i) != null) {
                    dimension.setIndicator(this.indicator);
                    dimension.setAccumulatedLine(this.accumulatedLine);

                    if (lineDimensionIndex != 0 && rootDimensionIndex == 0) {
                        rootDimensionIndex = -1;
                    }

                    dimension.writeTotalPartial(dimension.bottomDimensions.get(i), values, htmlLine, sequence, i, rootDimensionIndex, participParcialHorizontal, totalizedDimensions, horizontalParticipations);
                } else {
                    break;
                }
            }
        } else {

            CachedResults registroTotalizado = this.indicator.getTableRecords().getRegistroTotalizado();
            registroTotalizado.next();
            registroTotalizado = this.getRegisterTotal(values, registroTotalizado, sequence, totalizedDimensions, dimension);

            Field field;
            double sum;
            new HTMLCell();
            HTMLCell cell;

            HTMLStyle style = MultidimensionalStyles.getInstancia().getEstilos().get(MultidimensionalStyles.ESTILO_VAL_METRICA_LINHA);

            for (int i = 3; i < values[0].length; i++) {
                field = (Field) values[0][i];

                if (field.getDefaultField().equals("T")) {
                    continue;
                }

                if (field.isHorizontalAnalysis() && field.getTitle().contains("AH%") && lineDimensionIndex == 0 && rootDimensionIndex == 0) {
                    Field parentField = this.getParentField(field, values);
                    if (parentField.isPartialTotalization()) {
                        PartialTotalization partialTotalization = this.partialTotalizations.getTotalPartial(values, parentField);
                        if (partialTotalization != null) {
                            double fin = partialTotalization.getPartialTotalization();

                            if (parentField.isApplyTotalizationExpression()) {
                                Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, parentField, registroTotalizado);
                                fin = registroTotalizado.getDouble(parentField.getFieldId());
                            }
                            partialTotalization.setPartialTotalization(fin);
                            this.indicator.setPartialTotalizations(this.partialTotalizations);
                            List<Object> fieldValues = new ArrayList<>();
                            horizontalParticipations.put(parentField, fieldValues);
                            fieldValues.add(fin);
                        }
                    }
                } else {
                    if ((field.getFieldType() == null && !field.isTotalizingField()) || Constants.METRIC.equals(field.getFieldType())) {
                        cell = new HTMLCell();
                        htmlLine.addCell(cell);
                        cell.setNowrap(true);
                        cell.setStyle(style);
                        cell.setAlignment(field.getColumnAlignment());
                        if (field.isPartialTotalization()) {
                            if (!("".equals(field.getName()))) {
                                PartialTotalization partialTotalization = this.partialTotalizations.getTotalPartial(values, field);
                                sum = partialTotalization.getPartialTotalization();

                                if (field.isApplyTotalizationExpression()) {
                                    Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, field, registroTotalizado);
                                    sum = registroTotalizado.getDouble(field.getFieldId());
                                }
                                partialTotalization.setPartialTotalization(sum);
                                this.indicator.setPartialTotalizations(this.partialTotalizations);

                                cell.setContent(BIUtil.formatDoubleToText(sum, field.getNumDecimalPositions()));
                                this.indicator.getColorAlerts().searchAlertValueApply(sum, cell, htmlLine, field, ColorAlert.TOTALIZACAO_PARCIAL, 2, this, true);
                                this.indicator.getColorAlerts().searchOtherFieldAlertApply(sum, field, ColorAlert.TOTALIZACAO_PARCIAL, values, htmlLine.getCurrentCell(), htmlLine, field.getNumDecimalPositions(), this, dimension, registroTotalizado, 0, true);
                            }
                        } else {
                            String content = "-";
                            if (field.isChildField() && field.getTitle().startsWith("AH Participação")) {
                                Field parentField = this.getParentField(field, values);
                                if (parentField != null && this.partialTotalizations != null) {
                                    int index = this.getParentFieldIndex(field, values);

                                    PartialTotalization partialTotalization = this.partialTotalizations.getTotalPartial(values, parentField);
                                    if (partialTotalization != null) {
                                        sum = partialTotalization.getPartialTotalization();

                                        if (parentField.isApplyTotalizationExpression()) {
                                            Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, parentField, registroTotalizado);
                                            sum = registroTotalizado.getDouble(parentField.getFieldId());
                                        }
                                        partialTotalization.setPartialTotalization(sum);
                                        this.indicator.setPartialTotalizations(this.partialTotalizations);

                                        if (this.getAccumulatedLine(index) != 0) {
                                            if (field.getTitle().equals("AH Participação Acumulada")) {
                                                if (participParcialHorizontal == null) {
                                                    participParcialHorizontal = new HashMap<>();
                                                }
                                                if (participParcialHorizontal.get(parentField) != null) {
                                                    sum += Double.parseDouble((String) participParcialHorizontal.get(parentField));
                                                }
                                                participParcialHorizontal.put(parentField, String.valueOf(sum));
                                                this.indicator.getColorAlerts().searchAlertValueApply((sum / this.getAccumulatedLine(index)) * 100, cell, htmlLine, parentField, ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL, 2, this, true);
                                            } else {
                                                this.indicator.getColorAlerts().searchAlertValueApply((sum / this.getAccumulatedLine(index)) * 100, cell, htmlLine, parentField, ColorAlert.PARTICIPACAO_HORIZONTAL, 2, this, true);
                                            }
                                            content = BIUtil.formatDoubleToText(sum / this.getAccumulatedLine(index), 2);
                                        }
                                    }
                                }
                            } else if (field.isHorizontalAnalysis() && field.getTitle().contains("AH%")) {
                                Field parentField = this.getParentField(field, values);
                                if (parentField.isPartialTotalization()) {
                                    PartialTotalization partialTotalization = this.partialTotalizations.getTotalPartial(values, parentField);
                                    if (partialTotalization != null) {
                                        double fin = partialTotalization.getPartialTotalization();

                                        if (parentField.isApplyTotalizationExpression()) {
                                            Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, parentField, registroTotalizado);
                                            fin = registroTotalizado.getDouble(parentField.getFieldId());
                                        }
                                        partialTotalization.setPartialTotalization(fin);
                                        this.indicator.setPartialTotalizations(this.partialTotalizations);

                                        List<Object> fieldValues = horizontalParticipations.get(parentField);
                                        fieldValues.add(fin);
                                        double ini;
                                        if (Constants.DYNAMIC_HORIZONTAL_ANALYSIS.equals(parentField.getHorizontalAnalysisType())) {
                                            ini = (Double) fieldValues.get(fieldValues.size() - 2);
                                        } else {
                                            ini = (Double) fieldValues.get(0);
                                        }

                                        if (ini != fin) {
                                            if (ini != 0) {
                                                double res = (ini - fin) / ini;
                                                content = BIUtil.formatDoubleToText(-1 * res, 2);
                                            } else {
                                                if (fin > 0) {
                                                    content = BIUtil.formatDoubleToText(1, 2);
                                                } else if (fin < 0) {
                                                    content = BIUtil.formatDoubleToText(-1, 2);
                                                }
                                            }
                                        } else {
                                            content = BIUtil.formatDoubleToText(0, 2);
                                        }
                                    } else {
                                        content = "-";
                                    }
                                } else {
                                    content = "-";
                                }
                            } else {
                                if (field.isChildField() && (field.getTitle().startsWith("%"))) {
                                    if (this.partialTotalizations != null) {
                                        PartialTotalization partialTotalization = this.partialTotalizations.getTotalPartial(values, field);
                                        if (partialTotalization != null) {
                                            sum = partialTotalization.getPartialTotalization();

                                            if (field.isApplyTotalizationExpression()) {
                                                Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, field, registroTotalizado);
                                                sum = registroTotalizado.getDouble(field.getFieldId());
                                            }
                                            partialTotalization.setPartialTotalization(sum);
                                            this.indicator.setPartialTotalizations(this.partialTotalizations);
                                            content = BIUtil.formatDoubleToText(sum, 2);
                                        }
                                    }
                                }
                            }
                            cell.setAlignment(field.getColumnAlignment());
                            cell.setContent(content);
                        }
                    }
                }
            }
            if (htmlLine.isAppliedAlert()) {
                htmlLine.resetCellStyles();
            }
        }
        return total;
    }

    public Field getParentField(Field child, Object[][] values) {
        Field result = null;
        for (int i = 3; i < values[0].length; i++) {
            Field field = (Field) values[0][i];
            if (field != null) {
                if (field.getFieldId() == child.getFieldId() && !field.isChildField()) {
                    result = field;
                    break;
                }
            } else {
                break;
            }
        }
        return result;
    }

    public int getParentFieldIndex(Field child, Object[][] values) {
        int retorno = -1;
        for (int i = 3; i < values[0].length; i++) {
            Field campo = (Field) values[0][i];
            if (campo != null) {
                if (campo.getFieldId() == child.getFieldId() && !campo.isChildField()) {
                    retorno = i;
                    break;
                }
            } else {
                break;
            }
        }
        return retorno;
    }
    
    public CachedResults getRegisterTotal(Object[][] values, CachedResults registerTotal, int sequence, List<DimensionTotalized> dimensionsTotal, Dimension lineDimension) throws BIException {

        Field field;
        Field metricField = null;
        int metricIndex = 0;
        for (int ii = 3; ii < values[0].length; ii++) {
            field = (Field) values[0][ii];
            double somaParcial = 0;
            if ((field.getFieldType() == null && !field.isTotalizingField()) || Constants.METRIC.equals(field.getFieldType())) {

                if (field.isPartialTotalization() || field.isTotalizingField()) {
                    if (!("".equals(field.getName()))) {
                        metricField = field;
                        metricIndex = ii;
                        for (int iii = 1; iii < values.length; iii++) {
                            somaParcial += (Double) values[iii][ii];
                        }
                        this.setTotalization(field, values, sequence, somaParcial, registerTotal);
                    }
                }
                if (field.isVerticalAnalysis()) {
                    double somaParticipacao = 0;
                    double somaParametroPercentual = 0;

                    if (metricField != null && metricField.getVerticalAnalysisType().equals(Constants.VERTICAL_ANALYSIS_PARTIAL_TYPE)) {
                        this.getPercentageParameter(dimensionsTotal, metricField, lineDimension);
                        somaParametroPercentual = this.parentValue;
                        this.parentValue = 0;
                        this.parentValueFound = false;
                    } else {
                        if (values.length > 1)
                            somaParametroPercentual = (Double) values[1][metricIndex + 1];
                    }
                    if (!("".equals(field.getName()))) {
                        for (int iii = 1; iii < values.length; iii++) {
                            double valorField = (Double) values[iii][ii];
                            somaParticipacao += (valorField / somaParametroPercentual);
                        }
                        this.setTotalization((Field) values[0][ii + 2], values, sequence, somaParticipacao, registerTotal);
                    }
                }
            }
        }

        return registerTotal;
    }

    public double getGeneralDimensionValue(Field metric, Dimension dimension, List<DimensionTotalized> dimensionTotalRoot) {
        double valorTotal = 0;
        for (DimensionTotalized dimensionTotalized : dimensionTotalRoot) {
            PartialTotalization partialTotalization = dimensionTotalized.getPartialTotalization(metric, dimension);
            valorTotal += partialTotalization.getPartialTotalization();
        }
        return valorTotal;
    }

    private void getPercentageParameter(List<DimensionTotalized> totalizedList, Field campo, Dimension lineDimension) {
        for (int x = 0; x < totalizedList.size() && !this.parentValueFound; x++) {
            DimensionTotalized dimensionTotalized = totalizedList.get(x);
            if (dimensionTotalized != null) {
                if (dimensionTotalized.getChildDimensions() != null && !dimensionTotalized.getChildDimensions().isEmpty()) {
                    if (this.getDimensionValue().equals(dimensionTotalized.getDimensionValue()) && Arrays.deepEquals(this.getResults(), dimensionTotalized.getResults())) {
                        DimensionTotalized parent = (DimensionTotalized) dimensionTotalized.getParentDimension();

                        if (parent != null) {
                            this.parentValueFound = true;
                            PartialTotalization partialTotalization = parent.getPartialTotalization(campo, lineDimension);
                            this.parentValue = partialTotalization.getPartialTotalization();
                        } else {
                            this.parentValue = this.getGeneralDimensionValue(campo, lineDimension, totalizedList);
                        }
                    } else {
                        this.getPercentageParameter(dimensionTotalized.getChildDimensions(), campo, lineDimension);
                    }
                }
            }
        }
    }

    private void setTotalization(Field campo, Object[][] values, int sequence, double totalization, CachedResults totalizedRegister) throws BIException {
        PartialTotalization partialTotalization = new PartialTotalization();
        partialTotalization.setField(campo);
        partialTotalization.setValues(values);
        partialTotalization.setSequence(sequence);
        partialTotalization.setPartialTotalization(totalization);

        if (this.partialTotalizations == null) {
            this.partialTotalizations = new PartialTotalizations();
        }
        this.partialTotalizations.addToTotalPartial(partialTotalization);
        totalizedRegister.setDouble(totalization, campo.getFieldId());
        this.indicator.setPartialTotalizations(this.partialTotalizations);
    }

    public Object getDimensionValue() {
        return this.value.getValor(0);
    }

    public double getAccumulatedLine(int position) {
        return accumulatedLine[position];
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }


    public List<Field> getDimensionFields() {
        List<Field> result = new ArrayList<>();
        if (this.results != null && this.results[0] != null) {
            for (Object obj : this.results[0]) {
                if (obj instanceof Field) {
                    result.add((Field) obj);
                }
            }
        }
        return result;
    }

    public ConsultResult[] getConsultExpressionResult() {
        ConsultResult[] result;
        if (this.accumulatedLine != null) {
            result = new ConsultResult[this.accumulatedLine.length];
            int cont = 0;
            for (int i = 2; i < this.results[0].length; i++) {
                Field campo = (Field) this.results[0][i];
                if (campo.isMetric()) {
                    if (this.accumulatedLine != null) {
                        result[cont] = ConsultResultFactory.factory(campo);
                        result[cont++].addValor(this.accumulatedLine[i]);
                    }
                }
            }
        } else {
            result = null;
        }
        return result;
    }

    public double calAccExpression(Field field, int index) throws BIException {
        ConsultResult[] resultAux = this.getConsultExpressionResult();
        Double expressionValue = null;
        if (field != null && field.isExpression() && field.getAccumulatedLine().equals("E")) {
            Expression expression = indicator.getFieldExpression(field.getName());
            if (StringHelper.hasAllExpressionFields(this.getDimensionFields(), field, indicator)) {
                CachedResults cacheResult = new CachedResults(resultAux);
                cacheResult.next();
                if (field.isExpression() && (field.getName().toUpperCase().trim().indexOf("SE(") == 0 || field.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
                    ConditionalExpression expressionConditional = StringHelper.getExpressionConditional(field.getName(), indicator);
                    expressionValue = StringHelper.calculateConditionalExpression(expressionConditional, cacheResult);
                } else {
                    expressionValue = expression.calculaExpressao(cacheResult);
                }
                this.lineExpression[index] = expressionValue;
            }
        }
        if (expressionValue == null) {
            return 0;
        }
        return expressionValue;
    }

    public double calcExcelAccExpression(Field field, int index) throws BIException {
        ConsultResult[] resultAux = this.getConsultExpressionResult();
        Double expressionValue = null;
        if (field != null && field.isExpression() && field.getAccumulatedLine().equals("E")) {
            Expression expression = indicator.getFieldExpression(field.getName());
            if (StringHelper.hasAllExpressionFields(this.getDimensionFields(), field, indicator)) {
                CachedResults cacheResult = new CachedResults(resultAux);
                cacheResult.next();
                if (field.isExpression() && (field.getName().toUpperCase().trim().indexOf("SE(") == 0 || field.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
                    ConditionalExpression conditionalExpression = StringHelper.getExpressionConditional(field.getName(), indicator);
                    expressionValue = StringHelper.calculateConditionalExpression(conditionalExpression, cacheResult);
                } else {
                    expressionValue = expression.calculaExpressao(cacheResult);
                }
                this.lineExpression[index] = expressionValue;
            }
        }
        if (expressionValue == null) {
            return 0;
        }
        return expressionValue;
    }

    public int getColumnCount() {
        return this.bottomDimensions.stream()
                .filter(Objects::nonNull)
                .mapToInt(dimension -> dimension.hasLowerDimensions() ? dimension.getColumnCount() : 1)
                .sum();
    }

    public double getAccLineUpdated(int fieldIndex) {
        if (this.filterBySequence) {
            return 0;
        }

        if (this.hasLowerDimensions()) {
            return this.bottomDimensions.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(dimension -> dimension.getAccLineUpdated(fieldIndex))
                    .sum();
        } else if (this.accumulatedLine != null && !this.isFilterByAccumulated() && !this.isFilterBySequence()) {
            if (this.getValue().getField().isExpression()) {
                this.accumulatedLine[fieldIndex] = this.lineExpression[fieldIndex];
            }
            return this.accumulatedLine[fieldIndex];
        }

        return 0;
    }

    public double getAccLineOthers(int fieldIndex) {
        if (this.hasLowerDimensions()) {
            if (this.isFilterBySequence()) {
                return this.accumulatedLine[fieldIndex];
            } else {
                return this.bottomDimensions.stream()
                        .filter(Objects::nonNull)
                        .mapToDouble(dimension -> dimension.getAccLineOthers(fieldIndex))
                        .sum();
            }
        } else {
            if (this.isFilterBySequence() || this.isFilterByAccumulated()) {
                return this.accumulatedLine[fieldIndex];
            }
        }
        return 0;
    }

    public double getDimensionTotalValue(int fieldIndex) {
        return Arrays.stream(this.results)
                .skip(1)
                .mapToDouble(result -> (Double) result[fieldIndex])
                .sum();
    }

    public Object[][] applySequenceFilter(Object[][] results, boolean accFiltered, List<Dimension> lineDimensions, Dimension parentColumn) {
        if (this.hasLowerVisibleDimensions()) {
            List<Dimension> dimensions = this.bottomDimensions;
            for (Dimension dimension : this.bottomDimensions) {
                if (dimension != null) {
                    dimension.setIndicator(this.indicator);
                    results = dimension.applySequenceFilter(results, dimension.isFilterByAccumulated(), lineDimensions, parentColumn);
                    this.setFilterBySequence(true);
                } else {
                    break;
                }
            }
        } else {
            if (!accFiltered) {
                this.searchFilteredValues(results, lineDimensions, parentColumn, 1);
                this.clearDimensionResults();
                this.setFilterBySequence(true);
            }
        }
        return results;
    }

    public boolean validateAccumulated(Object[][] results, boolean sequenceFiltered, List<Dimension> dimensionLine, Dimension parentColumn, boolean anyDimensionFilter) {
        if (this.hasMoreLowerDimensions()) {
            List<Dimension> dimensions = this.bottomDimensions;
            int total = 0;
            int filtrado = 0;
            for (Dimension dimension : this.bottomDimensions) {
                if (dimension != null) {
                    total++;
                    dimension.setIndicator(this.indicator);
                    anyDimensionFilter = dimension.validateAccumulated(results, sequenceFiltered, dimensionLine, parentColumn, anyDimensionFilter);
                    if (dimension.isFilterByAccumulated() || dimension.isFilterBySequence()) {
                        filtrado++;
                    }
                } else {
                    break;
                }
            }
            if (total == filtrado && total > 0) {
                this.setFilterByAccumulated(true);
                anyDimensionFilter = true;
            }
        } else {
            boolean filtradoAcumulado = false;
            if (this.indicator.getFiltersFunction() != null && this.indicator.getFiltersFunction().getFilterAccumulated() != null) {
                FilterAccumulated filterAccumulated = this.indicator.getFiltersFunction().getFilterAccumulated();
                for (int i = 2; i < this.results[0].length; i++) {
                    Field campo = (Field) this.results[0][i];
                    if (campo.equals(filterAccumulated.getField()) && !campo.getAccumulatedLine().equals("N")) {
                        double value = this.getAccumulatedLine(i);
                        if (campo.isExpression() && campo.getAccumulatedLine().equals("E")) {
                            value = this.calculateFieldExpression(campo, i);
                        }
                        if (!filterAccumulated.verifyCondition(value)) {
                            filtradoAcumulado = true;
                            anyDimensionFilter = true;
                            break;
                        }

                    }
                }
            }
            if (filtradoAcumulado && !sequenceFiltered && !this.filterBySequence) {
                this.searchFilteredValues(results, dimensionLine, parentColumn, 2);
                this.clearDimensionResults();
            }
            this.setFilterByAccumulated(filtradoAcumulado);
        }
        return anyDimensionFilter;
    }

    public void searchFilteredValues(Object[][] results, List<Dimension> dimensionLines, Dimension parentColumn, int type) {
        for (Dimension currentDimension : dimensionLines) {
            Optional<Dimension> dimensionOptional = Optional.ofNullable(currentDimension);
            if (dimensionOptional.isPresent()) {
                Dimension dimension = dimensionOptional.get();
                if (dimension.hasLowerDimensions()) {
                    this.searchFilteredValues(results, dimension.getBottomDimensions(), parentColumn, type);
                } else {
                    Object[][] result = dimension.consult(this.results);
                    for (Object[] resultRow : result) {
                        Field field = (Field) resultRow[0];
                        if (field != null) {
                            for (int ii = 1; ii < result.length; ii++) {
                                if (field.isMetric() && field.isTotalizingField()) {
                                    double value = (Double) resultRow[ii];
                                    List<Dimension> dimensionKey = this.getLineDimensionKeys(dimension);
                                    this.removeTotalizationFieldValue(results, value, field, dimensionKey, parentColumn, type);
                                }
                            }
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    public void removeTotalizationFieldValue(Object[][] results, double value, Field valueField, List<Dimension> dimensionKey, Dimension parentDimension, int type) {
        final int NOT_FOUND = -1;
        Field referenceField = Optional.ofNullable(dimensionKey.get(0))
                .map(Dimension::getValue)
                .map(ConsultResult::getField)
                .orElse(null);

        int lineDimensionIndex = NOT_FOUND;
        int valueFieldIndex = NOT_FOUND;

        for (int i = 0; i < results[0].length; i++) {
            Field currentField = (Field) results[0][i];
            if (referenceField != null && referenceField.getFieldId() == currentField.getFieldId()) {
                lineDimensionIndex = i;
            } else if (valueField.equals(currentField)) {
                valueFieldIndex = i;
            }
        }

        if (lineDimensionIndex != NOT_FOUND && valueFieldIndex != NOT_FOUND) {
            for (int i = 1; i < results.length; i++) {
                if (results[i][lineDimensionIndex] != null) {
                    Iterator<Dimension> iterator = dimensionKey.iterator();
                    int index = lineDimensionIndex;
                    int count = 0;
                    Dimension currentDimension = iterator.next();

                    while (currentDimension != null && currentDimension.getValue().getValor(0).equals(results[i][index])) {
                        if (iterator.hasNext()) {
                            currentDimension = iterator.next();
                        }
                        index++;
                        count++;
                    }

                    if (count == dimensionKey.size()) {
                        double originalValue = (Double) results[i][valueFieldIndex + 1];
                        double finalValue = originalValue - value;
                        results[i][valueFieldIndex + 1] = finalValue;
                    }
                }
            }
        }
    }

    public void clearDimensionResults() {
        IntStream.range(1, this.results.length)
                .forEach(i -> IntStream.range(0, this.results[0].length)
                        .filter(j -> {
                            Field field = (Field) this.results[0][j];
                            return field.isMetric() && field.isPartialTotalization();
                        })
                        .forEach(j -> this.results[i][j] = 0.0));
    }

    public List<Dimension> getLineDimensionKeys(Dimension dimension) {
        List<Dimension> result = new ArrayList<>();
        Dimension current = dimension;
        while (current != null) {
            result.add(current);
            current = current.getParentDimension();
        }
        Collections.reverse(result);
        return result;
    }

    public double calculateFieldExpression(Field field, int i) {
        ConsultResult[] resultAux = this.getConsultExpressionResult();
        double valorDaExpressao = 0d;
        if (field != null && field.isExpression() && field.getAccumulatedLine().equals("E")) {
            Expression expression = indicator.getFieldExpression(field.getName());
            if (StringHelper.hasAllExpressionFields(this.getDimensionFields(), field, indicator)) {
                CachedResults cacheResult;
                try {
                    cacheResult = new CachedResults(resultAux);
                    cacheResult.next();
                    if (field.isExpression() && (field.getName().toUpperCase().trim().indexOf("SE(") == 0 || field.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
                        ConditionalExpression conditional = StringHelper.getExpressionConditional(field.getName(), indicator);
                        valorDaExpressao = StringHelper.calculateConditionalExpression(conditional, cacheResult);
                    } else {
                        valorDaExpressao = expression.calculaExpressao(cacheResult);
                    }
                    this.lineExpression[i] = valorDaExpressao;
                } catch (BIException e) {
                    log.error("Error calculating expression", e);
                }
            }
        }
        return valorDaExpressao;
    }

    public void validateSequence(FilterSequence sequenceFilter, List<Dimension> lineDimension, int columnResultCount, Dimension parentColumn, Object[][] results) {
        if ("S".equals(this.getValue().getField().getDefaultField())) {

            boolean filtradoSequencia = false;
            if (this.indicator.getFiltersFunction() != null && this.indicator.getFiltersFunction().getFilterSequence() != null && this.indicator.isUsesSequence()) {
                if (sequenceFilter != null) {
                    if (sequenceFilter.isRanking()) {
                        if (!sequenceFilter.verifyRanking(this.getCounter(), columnResultCount)) {
                            filtradoSequencia = true;
                        }
                    } else {
                        if (!sequenceFilter.verifyCondition(this.getCounter())) {
                            filtradoSequencia = true;
                        }
                    }
                }
            }
            if (filtradoSequencia) {
                this.applySequenceFilter(results, this.isFilterByAccumulated(), lineDimension, parentColumn);
                this.setFilterBySequence(filtradoSequencia);
            }
        } else {
            int total = 0;
            int filtrado = 0;
            List<Dimension> bottomDimensions = this.getBottomDimensions();
            for (Dimension dimension : bottomDimensions) {
                if (dimension != null) {
                    total++;
                    dimension.setIndicator(this.indicator);
                    dimension.validateSequence(sequenceFilter, lineDimension, columnResultCount, parentColumn, results);
                    if (dimension.isFilterBySequence() || dimension.isFilterByAccumulated()) {
                        filtrado++;
                    }
                } else {
                    break;
                }
            }
            if (total == filtrado && total > 0) {
                this.setFilterBySequence(true);
            }
        }
    }

    public int getColumnResultsCount() {
        int retorno = 0;
        for (Dimension dimension : this.bottomDimensions) {
            if (dimension != null) {
                if ("T".equals(dimension.getValue().getField().getDefaultField())) {
                    retorno += dimension.getColumnResultsCount();
                } else if (!dimension.isFilterByAccumulated()) {
                    retorno++;
                }
            } else {
                break;
            }
        }
        return retorno;
    }

    public int setDimensionCounter(int count) {
        for (Dimension dimension : this.bottomDimensions) {
            if (dimension != null) {
                dimension.setIndicator(this.indicator);
                if ("S".equals(dimension.getValue().getField().getDefaultField()) && !dimension.isFilterByAccumulated()) {
                    dimension.setCounter(count);
                    if (this.indicator.getSequeceValuesRepository() != null) {
                        this.indicator.getSequeceValuesRepository().addValue(dimension.getCounter());
                    }
                    dimension.setCounterEnabled(true);
                    count++;
                } else {
                    count = dimension.setDimensionCounter(count);
                }
            } else {
                break;
            }
        }
        return count;
    }

    public HashMap<String, Double> getTotalLines() {
        if (this.totalLines == null) {
            this.totalLines = new HashMap<>();
        }
        return this.totalLines;
    }

    public int getColumnLinesCount(List<Dimension> lineDimensions) {
        int contador = 0;
        for (Dimension biDimension : lineDimensions) {
            if (biDimension != null) {
                if (biDimension.hasLowerDimensions()) {
                    contador += biDimension.getColumnLinesCount(biDimension.getBottomDimensions());
                } else {
                    contador++;
                }
            } else {
                break;
            }
        }
        return contador;
    }

    public void applyLineColumnsCount(List<Dimension> dimensionColumns, int lineColumnsCount) {
        for (Dimension biDimension : dimensionColumns) {
            if (biDimension != null) {
                biDimension.setColumnLineAmount(lineColumnsCount);
                if (biDimension.hasLowerDimensions()) {
                    biDimension.applyLineColumnsCount(biDimension.getBottomDimensions(), lineColumnsCount);
                }
            } else {
                break;
            }
        }
    }

    public void applyLineSum(List<Dimension> dimensionColumns, double lineSum) {
        for (Dimension biDimension : dimensionColumns) {
            if (biDimension != null) {
                biDimension.setLineSum(lineSum);
                if (biDimension.hasLowerDimensions()) {
                    biDimension.applyLineSum(biDimension.getBottomDimensions(), lineSum);
                }
            } else {
                break;
            }
        }
    }

    public void setAlertLineStyle(boolean alertLineStyle) {
        this.alertLineStyle = alertLineStyle;
        if (this.hasLowerDimensions()) {
            for (Dimension bottonDimension : this.bottomDimensions) {
                if (bottonDimension != null) {
                    bottonDimension.setAlertLineStyle(alertLineStyle);
                } else {
                    break;
                }
            }
        }
    }

    public void setLineAppliedStyle(Object lineAppliedStyle) {
        this.lineAppliedStyle = lineAppliedStyle;
        if (this.hasLowerDimensions()) {
            for (Dimension bottonDimension : this.bottomDimensions) {
                if (bottonDimension != null) {
                    bottonDimension.setLineAppliedStyle(lineAppliedStyle);
                } else {
                    break;
                }
            }
        }
    }

    public int getMetricLinesWithAH(Field[] metricLines) {
        int retorno = 0;
        if (metricLines != null) {
            for (Field field : metricLines) {
                if (field != null && field.isHorizontalAnalysis() && field.getTitle().contains("AH%")) {
                    retorno++;
                }
            }
        }
        return retorno;
    }

    public void clearMemoryObject() {
        if (this.bottomDimensions != null) {
            for (Dimension biDimension : this.bottomDimensions) {
                if (biDimension != null) {
                    biDimension.clearMemoryObject();
                } else {
                    break;
                }
            }
            this.bottomDimensions = null;
        }

        this.columnDimension = null;

        this.lineDimension = null;

        this.value = null;
        this.parentDimension = null;

        this.results = null;
        this.index = 1;
        this.indicator = null;
        this.totalLines = null;
        this.horizontalParticipation = null;
        this.lineAppliedStyle = null;
    }

    public PartialTotalizations getPartialTotalizations() {
        if (this.partialTotalizations == null)
            this.partialTotalizations = new PartialTotalizations();
        return partialTotalizations;
    }

}
