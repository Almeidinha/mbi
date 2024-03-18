package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.indicator.Operators;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Getter
@Setter
@Log4j2
@NoArgsConstructor
@SuppressWarnings("unused")
public abstract class Condition {

    protected Map<Integer, Object> valuesMap;
    private int valueCount;
    private Field field;
    private Operator operator;
    private String value;
    private String SQLValue;

    private Condition(Field field, Operator operator, String value, int valueCount) throws BIException {
        this.valueCount = 0;
        this.operator = operator;
        this.value = value;
        this.field = field;
        this.SQLValue = "";
        this.valuesMap = new HashMap<>();
        this.init();
    }

    public Condition(Condition condition) throws BIException {
        this(condition.getField(), condition.getOperator(), condition.getValue(), 0);
    }

    public Condition(Field field, Operator operator, String value) throws BIException {
        this(field, operator, value, 0);
    }

    public Condition(Field field, String operator, String value) throws BIException {
        this(field, new Operator(operator), value, 0);
    }

    private void init() throws BIException {
        if (this.value != null && !this.value.isEmpty()) {
            this.value = processValue(value);
        }

        if (this.field != null && Constants.NUMBER.equals(this.field.getDataType())) {
            this.value = Optional.ofNullable(this.value).orElse("").replaceAll(",", ";");
        }

        this.SQLValue = formatSQLValue();
    }

    private String processValue(String value) {
        StringBuilder newValue = new StringBuilder();

        if (value.contains("'")) {
            newValue.append(processQuotedValue(value));
        } else {
            newValue.append(value);
        }

        return newValue.toString();
    }

    private String processQuotedValue(String value) {
        StringBuilder newValue = new StringBuilder();
        int startIndex = 0;
        int endIndex;

        while ((endIndex = value.indexOf('\'', startIndex)) != -1) {
            String unitValue = value.substring(startIndex + 1, endIndex);
            newValue.append(unitValue).append(";");
            startIndex = endIndex + 1;
        }

        if (!newValue.isEmpty()) {
            newValue.setLength(newValue.length() - 1);
        }

        return newValue.toString();
    }

    public String toString() {
        return this.toString(false, true);
    }

    public String toString(boolean isMetric, boolean isAggregation) {
        try {
            String op = this.mapOperator(this.operator.getSymbol());
            StringBuilder result;

            if (this.SQLValue.equalsIgnoreCase("null")) {
                op = (op.equals("IN(")) ? "IS" : "IS NOT";
            }

            if (isMetric) {
                result = this.handleMetric(isAggregation, op);
            } else {
                result = this.handleNonMetric(isAggregation, op);

            }

            return result.toString();
        } catch (BIException e) {
            log.error("Error: " + e.getMessage());
            return "";
        }
    }

    private StringBuilder handleNonMetric(boolean isAggregation, String op) {
        StringBuilder result = new StringBuilder();

        String fieldName = (!this.field.getTableNickName().isEmpty()) ?
                ((!isAggregation) ? "" : this.field.getTableNickName() + ".") + this.field.getName() : this.field.getName();

        result.append(fieldName).append(" ").append(op).append(" ");
        if (op.contains("IN(")) {
            result.append(this.getDimensionValuesIN(this.SQLValue)).append(")");
        } else {
            result.append(this.SQLValue);
        }

        return result;
    }

    private StringBuilder handleMetric(boolean isAggregation, String op) throws BIException {
        StringBuilder result = new StringBuilder();
        if (this.field.isExpression() && !this.field.getName().toUpperCase().trim().startsWith("SE(")
                && !this.field.getName().toUpperCase().trim().startsWith("IF(")
                && !this.checkHierarchyCalculate(this.field)) {

            String aggregator = (this.field.getAggregationType() != null && !"EMPTY".equalsIgnoreCase(this.field.getAggregationType())) ?
                    this.field.getAggregationType() : "SUM";

            if (isAggregation) {
                result.append(aggregator).append("(");
            }
            result.append(this.field.getIndicator().converteExpressaoDeCodigoParaApelidoNome(this.field.getName(), true))
                    .append(") ").append(op).append(" ?");
        } else {
            String fieldName = (!this.field.getTableNickName().isEmpty()) ?
                    this.field.getTableNickName() + "." + this.field.getName() : this.field.getName();

            result.append("(").append(fieldName).append(") ").append(op).append(" ").append(this.getMetricValuesIN(this.SQLValue));
        }

        return result;
    }

    private String mapOperator(String op) {
        return switch (op) {
            case Operators.EQUAL_TO -> "IN(";
            case Operators.NOT_EQUAL_TO -> "NOT IN(";
            case Operators.STARTS_WITH -> "LIKE";
            case Operators.NOT_CONTAINS -> "NOT LIKE";
            default -> op;
        };
    }


    private boolean checkHierarchyCalculate(Field field) throws BIException {
        Indicator indicator = field.getIndicator();
        String nome = field.getName();
        String expression = indicator.converteExpressaoDeCodigoParaTitulo(nome);
        return (expression != null && !(!expression.toUpperCase().contains("SE(") || !expression.toUpperCase().contains("IF(")));
    }


    private String getMetricValuesIN(String sqlValue) {
        /* TODO was this necessarie?
        if (sqlValue.contains(",")) {
            sqlValue = sqlValue.replaceAll(",", ";");
        }
        * */
        if (Optional.ofNullable(sqlValue).isEmpty()) {
            return "";
        }

        String processedSQLValue = sqlValue.contains(",") ? sqlValue.replaceAll(",", ";") : sqlValue;
        List<String> values = BIUtil.stringtoList(processedSQLValue, ";");

        return String.join(", ", Collections.nCopies(values.size(), "?"));
    }

    private String getDimensionValuesIN(String sqlValue) {
        if (Optional.ofNullable(sqlValue).isEmpty() || sqlValue.isEmpty()) {
            return "";
        }

        List<String> values = BIUtil.stringtoList(sqlValue, ";");
        return String.join(", ", values);
    }

    public String formatSQLValue() throws BIException {
        StringBuilder result = new StringBuilder(this.value);
        if (!StringUtils.equalsIgnoreCase(result.toString(), "null") && this.field != null) {
            List<String> values = BIUtil.stringtoList(result.toString(), ";");
            result.setLength(0);

            for (String sValue : values) {
                result.append("?").append(",");
                this.valuesMap.put(++this.valueCount, sValue);
            }
            if (!values.isEmpty()) {
                result.setLength(result.length() - 1);
            }
        }
        return result.toString();
    }

    public String toStringWithCode(boolean metric) {
        String op = this.mapOperator(this.operator.getSymbol());
        StringBuilder result = new StringBuilder();

        if (this.SQLValue.equalsIgnoreCase("null")) {
            op = op.equals("IN(") ? "IS" : "IS NOT";
        }

        result.append("#$").append(this.field.getCode()).append("$! ").append(op).append(" ").append(this.SQLValue);

        if (op.contains("IN(")) {
            result.append(")");
        }

        return result.toString();
    }

    protected abstract Object clone() throws CloneNotSupportedException;

    protected abstract Object format(String value) throws BIException;

    protected abstract Object applyValues(Object stmt, int position) throws BIException;

    public abstract String getFormattedValue();
}
