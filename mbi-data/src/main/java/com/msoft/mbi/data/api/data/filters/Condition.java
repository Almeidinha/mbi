package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.htmlbuilder.StringHelper;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.indicator.Operators;
import com.msoft.mbi.data.api.data.util.BIUtil;
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
        StringBuilder newValue = new StringBuilder();
        if (!this.value.isEmpty()) {
            if (this.value.contains("'")) {
                newValue.append(this.value.replace("'", "").replace(",", "; "));
            } else {
                newValue.append(this.value.replace(",", "; "));
            }
            this.value = newValue.toString();
        }
        this.SQLValue = this.formatSQLValue();
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
            log.error("Error in Condition.toString(): " + e.getMessage());
            return "";
        }
    }

    private StringBuilder handleNonMetric(boolean isAggregation, String op) {
        StringBuilder result = new StringBuilder();

        String fieldName = (!this.field.getTableNickname().isEmpty()) ?
                ((!isAggregation) ? "" : this.field.getTableNickname() + ".") + this.field.getName() : this.field.getName();

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
            result.append(StringHelper.convertExpressionCodeToNickName(this.field.getName(), true, this.field.getIndicator()))
                    .append(") ").append(op).append(" ?");
        } else {
            String fieldName = (!this.field.getTableNickname().isEmpty()) ?
                    this.field.getTableNickname() + "." + this.field.getName() : this.field.getName();

            result.append("(").append(fieldName).append(") ").append(op).append(" ");

            if (op.contains("IN(")) {
                result.append(this.getMetricValuesIN(this.SQLValue)).append(")");
            } else {
                result.append(this.getMetricValuesIN(this.SQLValue));
            }

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
        String expression = StringHelper.convertCodeExpressionToTitle(nome, indicator);
        return (expression != null && !(!expression.toUpperCase().contains("SE(") || !expression.toUpperCase().contains("IF(")));
    }


    private String getMetricValuesIN(String sqlValue) {
        if (Optional.ofNullable(sqlValue).isEmpty()) {
            return "";
        }

        String processedSQLValue = sqlValue.contains(",") ? sqlValue.replaceAll(",", ";") : sqlValue;
        List<String> values = BIUtil.stringToList(processedSQLValue, ";");

        return String.join(", ", Collections.nCopies(values.size(), "?"));
    }

    private String getDimensionValuesIN(String sqlValue) {
        if (Optional.ofNullable(sqlValue).isEmpty() || sqlValue.isEmpty()) {
            return "";
        }

        List<String> values = BIUtil.stringToList(sqlValue, ";");
        return String.join(", ", values);
    }

    public String formatSQLValue() throws BIException {
        StringBuilder result = new StringBuilder(this.value);
        if (!StringUtils.equalsIgnoreCase(result.toString(), "null") && this.field != null) {
            List<String> values = BIUtil.stringToList(result.toString(), ";");
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

        result.append("#$").append(this.field.getFieldId()).append("$! ").append(op).append(" ").append(this.SQLValue);

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
