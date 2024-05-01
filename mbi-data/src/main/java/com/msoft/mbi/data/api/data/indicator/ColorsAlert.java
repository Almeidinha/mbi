package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.consult.CachedResults;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.DateException;
import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.FilterFactory;
import com.msoft.mbi.data.api.data.htmlbuilder.Cell;
import com.msoft.mbi.data.api.data.htmlbuilder.HTMLStyle;
import com.msoft.mbi.data.api.data.htmlbuilder.Line;
import com.msoft.mbi.data.api.data.htmlbuilder.LinkHTML;
import com.msoft.mbi.data.api.data.util.BIData;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Log4j2
@Setter
public class ColorsAlert {

    private List<ColorAlert> colorAlertList;

    public List<ColorAlert> getColorAlertList() {
        if (colorAlertList == null) {
            colorAlertList = new ArrayList<>();
        }
        return colorAlertList;
    }

    public void addToColorAlertList(ColorAlert colorAlert) {
        this.getColorAlertList().add(colorAlert);
    }

    public List<ColorAlert> getColorAlertListValues() {
        ArrayList<ColorAlert> list = new ArrayList<>();
        for (ColorAlert colorsAlert : this.getColorAlertList()) {
            if (colorsAlert != null && !colorsAlert.isCompareToAnotherField()) {
                list.add(colorsAlert);
            }
        }
        return list;
    }

    public List<ColorAlert> getOtherFieldsAlertList() {
        ArrayList<ColorAlert> list = new ArrayList<>();
        for (ColorAlert colorsAlert : this.getColorAlertList()) {
            if (colorsAlert != null && colorsAlert.isCompareToAnotherField()) {
                list.add(colorsAlert);
            }
        }
        return list;
    }

    public void remove(ColorAlert colorAlert) {
        this.getColorAlertList().remove(colorAlert);
        int index = 0;
        for (ColorAlert aux : this.getColorAlertList()) {
            aux.setSequence(index++);
        }
    }

    public void setIndicator(Indicator indicator) {
        for (ColorAlert colorAlert : this.getColorAlertList()) {
            if (colorAlert != null) {
                colorAlert.setIndicator(indicator);
            }
        }
    }

    public boolean searchAlertValueApply(Object value, Cell cell, Line line, Field field, String function, int decimalPositions, Dimension dimension, boolean isHtml) throws BIException, DateException {
        boolean retorno = false;
        ColorAlert colorAlert;
        List<ColorAlert> colorsAlert = this.getFieldAlertFunctionValueList(function, field);
        for (ColorAlert alert : colorsAlert) {
            colorAlert = alert;
            if (colorAlert != null && !isrestrictByLine(colorAlert.getAction(), dimension)) {
                Operator operator = colorAlert.getOperator();

                if (field == null || field.getFieldType().equals(Constants.METRIC)) {
                    value = BIUtil.formatDoubleValue(String.valueOf(value), decimalPositions);
                    double valComp = BIUtil.formatDoubleValue(colorAlert.getFirstDoubleValue(), decimalPositions);
                    double segundoValor = 0;
                    if (colorAlert.getSecondValue() != null) {
                        segundoValor = BIUtil.formatDoubleValue(colorAlert.getSegundoValorDouble(), decimalPositions);
                    }
                    if (this.compareDoubleValue((Double) value, operator, valComp, segundoValor)) {
                        Object style = this.applyProperties(colorAlert, cell, line, isHtml, Constants.DIMENSION.equals(colorAlert.getFirstField().getFieldType()));
                        if (colorAlert.getAction().equals(ColorAlert.LINHA)) {
                            dimension.setLineAppliedStyle(style);
                            retorno = true;
                        }
                        break;
                    }
                } else {
                    if (!field.getDataType().equals(Constants.DATE)) {
                        String valorComp = colorAlert.getFirstValue();
                        if (valorComp.trim().startsWith("@|") && valorComp.trim().endsWith("|")) {
                            DimensionFilter dimensionFilter = FilterFactory.createDimensionFilter(field, colorAlert.getOperator().getSymbol(), valorComp);
                            Condition condition = dimensionFilter.getCondition();
                            valorComp = condition.getValue();
                        }
                        if (this.compareStringValue((String) value, operator, valorComp)) {
                            Object style = this.applyProperties(colorAlert, cell, line, isHtml, Constants.DIMENSION.equals(colorAlert.getFirstField().getFieldType()));
                            if (colorAlert.getAction().equals(ColorAlert.LINHA)) {
                                dimension.setLineAppliedStyle(style);
                                retorno = true;
                            }
                            break;
                        }
                    } else {
                        String dataString = colorAlert.getFirstValue();
                        String dataString2 = colorAlert.getSecondValue();
                        if (value != null && !value.toString().trim().isEmpty()) {
                            BIData data = new BIData(value.toString(), BIData.DAY_MONTH_YEAR_4DF);
                            if (dataString.trim().startsWith("@|") && dataString.trim().endsWith("|")) {
                                DimensionFilter dimensionFilter = FilterFactory.createDimensionFilter(field, colorAlert.getOperator().getSymbol(), dataString);
                                if (dimensionFilter.getFilters() != null && !dimensionFilter.getFilters().isEmpty()) {
                                    boolean allTrue = dimensionFilter.getFilters().stream()
                                            .filter(Objects::nonNull)
                                            .filter(filter -> filter.getCondition() != null)
                                            .allMatch(filter -> {
                                                Condition condition = filter.getCondition();
                                                BIData biData = new BIData(condition.getFormattedValue(), BIData.DAY_MONTH_YEAR_4DF);
                                                return compareDateValue(data, condition.getOperator(), biData, null);
                                            });
                                    if (allTrue) {
                                        Object style = this.applyProperties(colorAlert, cell, line, isHtml, Constants.DIMENSION.equals(colorAlert.getFirstField().getFieldType()));
                                        if (colorAlert.getAction().equals(ColorAlert.LINHA)) {
                                            dimension.setLineAppliedStyle(style);
                                            retorno = true;
                                        }
                                        break;
                                    }
                                } else {
                                    Condition condition = dimensionFilter.getCondition();
                                    BIData biData = new BIData(condition.getFormattedValue(), BIData.DAY_MONTH_YEAR_4DF);
                                    if (compareDateValue(data, condition.getOperator(), biData, null)) {
                                        Object style = this.applyProperties(colorAlert, cell, line, isHtml, Constants.DIMENSION.equals(colorAlert.getFirstField().getFieldType()));
                                        if (colorAlert.getAction().equals(ColorAlert.LINHA)) {
                                            dimension.setLineAppliedStyle(style);
                                            retorno = true;
                                        }
                                    }
                                }
                            } else {
                                BIData biData = new BIData(dataString.trim(), BIData.DAY_MONTH_YEAR_4DF);
                                BIData dataComparacao2 = null;
                                if (colorAlert.getOperator().getSymbol().equals(Operators.BETWEEN)) {
                                    dataComparacao2 = new BIData(dataString2.trim(), BIData.DAY_MONTH_YEAR_4DF);
                                }

                                if (compareDateValue(data, colorAlert.getOperator(), biData, dataComparacao2)) {
                                    Object style = this.applyProperties(colorAlert, cell, line, isHtml, Constants.DIMENSION.equals(colorAlert.getFirstField().getFieldType()));
                                    if (colorAlert.getAction().equals(ColorAlert.LINHA)) {
                                        dimension.setLineAppliedStyle(style);
                                        retorno = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return retorno;
    }

    private List<ColorAlert> getFieldAlertFunctionValueList(String function, Field field) {
        return this.getColorAlertListValues().stream()
                .filter(Objects::nonNull)
                .filter(colorAlert -> function.trim().equals(colorAlert.getFirstFieldFunction().trim()))
                .filter(colorAlert -> ColorAlert.TOTALIZACAO_HORIZONTAL.equals(function.trim())
                        || ColorAlert.TOTALIZACAO.equals(function.trim())
                        || field.equals(colorAlert.getFirstField()))
                .collect(Collectors.toList());
    }

    public boolean searchOtherFieldAlertApply(double value, Field firstField, String firstFieldFunction, Object[][] values, Cell cell, Line line,
                                              int decimalPositions, Dimension columnDimension, Dimension lineDimension, CachedResults cachedResults,
                                              int comparisonType, boolean isHtml) {
        return getAlerts(firstFieldFunction, firstField).stream()
                .filter(Objects::nonNull)
                .filter(alert -> isApplicable(alert, firstField, columnDimension))
                .anyMatch(alert -> applyAlert(alert, value, firstField, firstFieldFunction, values, cell, line, decimalPositions, columnDimension, lineDimension, cachedResults, comparisonType, isHtml));
    }

    private List<ColorAlert> getAlerts(String firstFieldFunction, Field firstField) {
        return Optional.of(this.getOtherFieldAlertFunctionList(firstFieldFunction, firstField))
                .orElse(new ArrayList<>());
    }

    private boolean isApplicable(ColorAlert alert, Field firstField, Dimension columnDimension) {
        return firstField == null || Constants.METRIC.equals(firstField.getFieldType())
                && alert.getSecondField() != null && "S".equals(alert.getSecondField().getDefaultField())
                && !isrestrictByLine(alert.getAction(), columnDimension);
    }

    private boolean applyAlert(ColorAlert alert, double value, Field firstField, String firstFieldFunction, Object[][] values, Cell cell, Line line,
                               int decimalPositions, Dimension columnDimension, Dimension lineDimension, CachedResults cachedResults,
                               int comparisonType, boolean isHtml) {
        try {

            value = BIUtil.formatDoubleValue(value, decimalPositions);
            String functionField = alert.getFirstField().equals(alert.getSecondField()) ? alert.getSecondFieldFunction() : alert.getFirstFieldFunction();
            double comparisonValue = BIUtil.formatDoubleValue(getComparisonValue(alert, firstField, values, functionField, columnDimension, lineDimension, cachedResults, comparisonType), decimalPositions);
            return this.applyOtherFieldsAlert(alert, value, comparisonValue, decimalPositions, cell, line, isHtml, columnDimension) && ColorAlert.LINHA.equals(alert.getAction());

        } catch (BIException e) {
            log.error("Error applying alert", e);
            return false;
        }

    }

    private double getComparisonValue(ColorAlert alert, Field firstField, Object[][] values, String functionField, Dimension columnDimension, Dimension lineDimension, CachedResults cachedResults, int comparisonType) throws BIException {
        return this.getComparisonValue(alert.getSecondField(), firstField, values, functionField, columnDimension, lineDimension, alert.getIndicator(), cachedResults, comparisonType, alert.getFirstField().equals(alert.getSecondField()));
    }

    private double getComparisonValue(Field field, Field mainField, Object[][] values, String fieldFunction, Dimension dimensionColumn,
                                      Dimension dimensionLine, Indicator indicator, CachedResults cachedResults, int comparisonType,
                                      boolean sameField) throws BIException {
        double[] accumulatedLine = dimensionColumn.getAccumulatedLine();
        HashMap<String, Double> totalizedLines = dimensionColumn.getTotalLines();
        HashMap<Field, Double> horizontalParticipation = dimensionColumn.getHorizontalParticipation();

        double retorno = -1;
        int fieldIndex = getFieldIndex(values, field);
        int mainFieldIndex = getFieldIndex(values, mainField);

        switch (fieldFunction.trim()) {
            case ColorAlert.SEM_FUNCAO -> retorno = (Double) values[1][fieldIndex];
            case ColorAlert.ANALISE_HORIZONTAL -> {
                if (field.isHorizontalAnalysis()) {
                    for (int i = fieldIndex + 1; values[0][i] != null; i++) {
                        if (((Field) values[0][i]).isHorizontalAnalysis()) {
                            if (values[1][i] != null) {
                                double ini, fin, res;
                                fin = (Double) values[1][fieldIndex];
                                ini = (Double) values[1][i];
                                if (ini != 0) {
                                    res = (ini - fin) / ini;
                                    retorno = (res * -1) * 100;
                                } else {
                                    retorno = 0;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            case ColorAlert.PARTICIPACAO_HORIZONTAL -> {
                if (field.isHorizontalParticipation()) {
                    double ini = (Double) values[1][fieldIndex];
                    double total = totalizedLines.get(String.valueOf(field.getFieldId()));
                    if (total != 0) {
                        retorno = (ini / total) * 100;
                    }
                }
            }
            case ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL -> {
                if (field.isHorizontalParticipationAccumulated()) {
                    double valAux;
                    double ini = (Double) values[1][fieldIndex];
                    double total = totalizedLines.get(String.valueOf(field.getFieldId()));

                    if (ini != 0) {
                        if (horizontalParticipation.get(field) != null) {
                            valAux = horizontalParticipation.get(field);
                            if (total != 0) {
                                retorno = (ini / total + valAux) * 100;
                            }
                        } else {
                            if (total != 0) {
                                retorno = (ini / total) * 100;
                            }
                        }
                    } else {
                        retorno = 0;
                    }
                }
            }
            case ColorAlert.ACUMULADO_HORIZONTAL -> {
                if (!"N".equals(field.getAccumulatedLine()) && accumulatedLine.length > fieldIndex) {
                    double valorComp = accumulatedLine[fieldIndex];
                    if (field.getAccumulatedLine().equals("E")) {
                        valorComp = dimensionColumn.calAccExpression(field, fieldIndex);
                    }
                    retorno = valorComp;
                }
            }
            case ColorAlert.MEDIA_HORIZONTAL -> {
                if (field.isMediaLine()) {
                    double valorComp = accumulatedLine[fieldIndex];
                    if (field.getAccumulatedLine().equals("E")) {
                        valorComp = dimensionColumn.calAccExpression(field, fieldIndex);
                    }
                    retorno = valorComp / dimensionColumn.getColumnLineAmount();
                }
            }
            case ColorAlert.ANALISE_VERTICAL -> {
                if (field.isVerticalAnalysis()) {
                    double perc = (Double) values[1][fieldIndex] / (Double) values[1][fieldIndex + 1];
                    retorno = perc * 100;
                }
            }
            case ColorAlert.PARTICIPACAO_ACUMULADA -> {
                if (field.isAccumulatedParticipation()) {
                    Double soma = (Double) values[1][fieldIndex];
                    if (mainFieldIndex > fieldIndex) {
                        soma = 0d;
                    }
                    soma = dimensionColumn.updateTest(field, values[1], soma, fieldIndex);
                    Double valor = (Double) values[1][fieldIndex + 1];
                    if (valor != 0) {
                        retorno = (soma / valor) * 100;
                    } else {
                        retorno = 0;
                    }
                }
            }
            case ColorAlert.ACUMULADO_VERTICAL -> {
                if (field.isAccumulatedValue()) {
                    Double soma = (Double) values[1][fieldIndex];
                    if (mainFieldIndex > fieldIndex) {
                        soma = 0d;
                    }
                    soma = dimensionColumn.updateTest(field, values[1], soma, fieldIndex);
                    retorno = soma;
                }
            }
            case ColorAlert.TOTALIZACAO_VERTICAL -> {
                if (field.isTotalizingField()) {
                    for (int k = 0; k < values[0].length; k++) {
                        if (values[0][k] != null && values[0][k].equals(field)) {
                            retorno = (Double) values[1][k + 1];
                            if (field.isExpression() && field.isApplyTotalizationExpression() && cachedResults != null) {
                                Expression.aplicaExpressaoNoRegistroTotalizado(indicator, field, cachedResults);
                                retorno = Double.parseDouble(cachedResults.getValor(field.getFieldId()));
                            }
                            break;
                        }
                    }
                }
            }
            case ColorAlert.TOTALIZACAO_PARCIAL -> {
                if (field.isPartialTotalization()) {
                    double soma;
                    if (sameField) {
                        values = dimensionColumn.consult(values);
                    }
                    PartialTotalization totalPartial = indicator.getPartialTotalizations().getTotalPartial(values, field);
                    if (totalPartial != null) {
                        soma = totalPartial.getPartialTotalization();
                        if (field.isApplyTotalizationExpression()) {
                            Expression.aplicaExpressaoNoRegistroTotalizado(indicator, field, cachedResults);
                            soma = cachedResults.getDouble(field.getFieldId());
                        }
                        retorno = soma;
                    }
                }
            }
            case ColorAlert.TOTALIZACAO_HORIZONTAL -> retorno = dimensionColumn.getTotalLine();
            case ColorAlert.TOTALIZACAO -> retorno = dimensionColumn.getLineSum();
        }
        return retorno;
    }

    private boolean applyOtherFieldsAlert(ColorAlert colorAlert, double value, double valComp, int decimalPositions,
                                          Cell cell, Line line, boolean isHtml, Dimension dimension) {
        boolean result = false;
        Operator operator = colorAlert.getOperator();
        double auxValue = BIUtil.formatDoubleValue(colorAlert.getSecondFieldFunction(), decimalPositions);

        boolean apply;
        if ("M".equals(colorAlert.getValueType())) {
            apply = this.compareOtherFieldDouble(value, operator, valComp, auxValue);
        } else {
            apply = this.compareOtherFieldDoublePercentage(value, operator, valComp, auxValue);
        }
        if (apply) {
            Object style = this.applyProperties(colorAlert, cell, line, isHtml, Constants.DIMENSION.equals(colorAlert.getFirstField().getFieldType()));
            if (ColorAlert.LINHA.equals(colorAlert.getAction())) {
                dimension.setLineAppliedStyle(style);
                result = true;
            }
        }
        return result;
    }

    private List<ColorAlert> getOtherFieldAlertFunctionList(String function, Field field) {
        List<ColorAlert> result = new ArrayList<>();
        for (ColorAlert colorAlert : this.getOtherFieldsAlertList()) {
            if (colorAlert != null && function.trim().equals(colorAlert.getFirstFieldFunction().trim())) {
                if (ColorAlert.TOTALIZACAO_HORIZONTAL.equals(function.trim()) || ColorAlert.TOTALIZACAO.equals(function.trim())) {
                    result.add(colorAlert);
                } else {
                    if (field.equals(colorAlert.getFirstField())) {
                        result.add(colorAlert);
                    }
                }
            }
        }
        return result;
    }

    private Object applyProperties(ColorAlert colorAlert, Cell cell, Line line, boolean ehHTML, boolean isDimension) {
        Object style = this.createStyle(colorAlert, ehHTML, cell);

        if (colorAlert.getAction().equals(ColorAlert.LINHA)) {
            line.setStyle(style, isDimension);
            line.setAppliedAlert(true);
            line.setAppliedStyle(style);

            List<Cell> cells = line.getCells();
            if (cells != null) {
                for (Cell cellAux : cells) {
                    if (cellAux != null) {
                        if (!cellAux.isAppliedAlert() && !cellAux.isDimensionColumn()) {
                            cellAux.setStyle(null);
                            cellAux.setCellClass("");
                            cellAux.setAppliedAlert(true);
                            if (cellAux.getContent().getClass().getName().endsWith("HTML")) {
                                LinkHTML link = (LinkHTML) cellAux.getContent();
                                link.setCellClass("");
                                link.setStyle((HTMLStyle) style);
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        } else {
            cell.setStyle(style);
            if (cell.getContent().getClass().getName().endsWith("HTML")) {
                LinkHTML link = (LinkHTML) cell.getContent();
                link.setCellClass("");
                link.setStyle((HTMLStyle) style);
            }
            cell.setAppliedAlert(true);
        }
        return style;
    }


    public Object createStyle(ColorAlert colorAlert, boolean isHtml, Cell cell) {
        return createHtlStyle(colorAlert);
    }

    public Object createHtlStyle(ColorAlert colorAlert) {
        HTMLStyle style = new HTMLStyle();
        style.setFontFamily(colorAlert.getAlertProperty().getFontName());
        style.setFontSize(colorAlert.getAlertProperty().getFontSize());
        style.setFontColor(colorAlert.getAlertProperty().getFontColor());
        style.setBackgroundColor(colorAlert.getAlertProperty().getCellBackgroundColor());

        if (colorAlert.getAlertProperty().hasBold()) {
            style.setFontWeight("bold");
        }
        if (colorAlert.getAlertProperty().hasItalic()) {
            style.setFontStyle("italic");
        }

        return style;
    }

    private boolean compareDoubleValue(double value, Operator operator, double valorComp, double segundoValor) {
        Map<String, BiPredicate<Double, Double>> operatorActions = Map.of(
                Operators.GREATER_THAN, (v, vc) -> v > vc,
                Operators.GREATER_TAN_OR_EQUAL, (v, vc) -> v >= vc,
                Operators.EQUAL_TO, Double::equals,
                Operators.LESS_THAN, (v, vc) -> v < vc,
                Operators.LESS_THAN_OR_EQUAL, (v, vc) -> v <= vc,
                Operators.NOT_EQUAL_TO, (v, vc) -> !v.equals(vc),
                Operators.BETWEEN, (v, vc) -> v >= vc && v <= segundoValor
        );

        return operatorActions.getOrDefault(operator.getSymbol().trim(), (v, vc) -> false).test(value, valorComp);
    }

    private boolean compareOtherFieldDouble(double value, Operator operator, double valorComp, double valorAuxiliar) {
        Map<String, BiPredicate<Double, Double>> operatorActions = Map.of(
                Operators.GREATER_THAN, (v, va) -> v - va > valorAuxiliar,
                Operators.GREATER_TAN_OR_EQUAL, (v, va) -> v - va >= valorAuxiliar,
                Operators.EQUAL_TO, (v, va) -> v == (va + valorAuxiliar),
                Operators.LESS_THAN, (v, va) -> va - v > valorAuxiliar,
                Operators.LESS_THAN_OR_EQUAL, (v, va) -> va - v >= valorAuxiliar,
                Operators.NOT_EQUAL_TO, (v, va) -> v != (va + valorAuxiliar)
        );

        return operatorActions.getOrDefault(operator.getSymbol().trim(), (v, va) -> false).test(value, valorComp);
    }

    private boolean compareDateValue(BIData data, Operator operator, BIData dateCompare, BIData dateCompareTwo) {
        int comparison = data.compareTo(dateCompare);
        return switch (operator.getSymbol().trim()) {
            case Operators.GREATER_THAN -> comparison > 0;
            case Operators.GREATER_TAN_OR_EQUAL -> comparison >= 0;
            case Operators.EQUAL_TO -> comparison == 0;
            case Operators.LESS_THAN -> comparison < 0;
            case Operators.LESS_THAN_OR_EQUAL -> comparison <= 0;
            case Operators.BETWEEN -> comparison >= 0 && Optional.ofNullable(dateCompareTwo)
                    .map(data::compareTo)
                    .map(compare -> compare <= 0)
                    .orElse(false);
            case Operators.NOT_EQUAL_TO -> comparison != 0;
            default -> false;
        };
    }

    private boolean compareOtherFieldDoublePercentage(double value, Operator operator, double valorComp, double valorAuxiliar) {
        valorAuxiliar = (valorAuxiliar / 100) * valorComp;

        Map<String, BiPredicate<Double, Double>> operatorActions = Map.of(
                Operators.GREATER_THAN, (v, va) -> v > va,
                Operators.GREATER_TAN_OR_EQUAL, (v, va) -> v >= va,
                Operators.EQUAL_TO, Double::equals,
                Operators.LESS_THAN, (v, va) -> v < va,
                Operators.LESS_THAN_OR_EQUAL, (v, va) -> v <= va,
                Operators.NOT_EQUAL_TO, (v, va) -> !v.equals(va)
        );

        return operatorActions.getOrDefault(operator.getSymbol().trim(), (v, va) -> false).test(value, valorAuxiliar);
    }

    private boolean compareStringValue(String value, Operator operator, String valorComp) {
        Map<String, BiPredicate<String, String>> operatorActions = Map.of(
                Operators.EQUAL_TO, String::equals,
                Operators.STARTS_WITH, String::startsWith,
                Operators.CONTAINS, String::contains,
                Operators.NOT_CONTAINS, (v, vc) -> !v.contains(vc),
                Operators.NOT_EQUAL_TO, (v, vc) -> !v.equals(vc)
        );

        return operatorActions.getOrDefault(operator.getSymbol().trim(), (v, vc) -> false).test(value.trim(), valorComp.trim());
    }

    private int getFieldIndex(Object[][] values, Field field) {
        return IntStream.range(0, values[0].length)
                .filter(i -> values[0][i] != null && field.equals(values[0][i]))
                .findFirst()
                .orElse(-1);
    }

    public boolean isrestrictByLine(String action, Dimension dimension) {
        return ColorAlert.LINHA.equals(action) && dimension.isAlertLineStyle();
    }

    public ColorsAlert clone(Indicator indicator) {
        ColorsAlert colorsAlert = new ColorsAlert();
        colorsAlert.setColorAlertList(new ArrayList<>());
        if (this.colorAlertList != null) {
            this.colorAlertList.stream()
                    .filter(Objects::nonNull)
                    .map(alert -> alert.copy(indicator))
                    .forEach(colorsAlert::addToColorAlertList);
        }
        return colorsAlert;
    }
}
