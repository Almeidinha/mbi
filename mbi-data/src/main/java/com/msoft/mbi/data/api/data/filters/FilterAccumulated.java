package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.indicator.Operators;
import com.msoft.mbi.data.api.data.util.BIUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Getter
@Setter
public class FilterAccumulated implements FilterFunction, Cloneable {


    private Operator operator;
    private String value;
    private int type;
    private Field field;

    public FilterAccumulated(Operator operator, String value, Field field) {
        this.operator = operator;
        this.value = value;
        this.type = this.ACCUMULATED_FILTER;
        this.field = field;
    }

    private List<Double> getDoubleValues() {
        List<Double> result = new ArrayList<>();
        List<String> values = BIUtil.stringToList(this.value, ";");
        for (String sValor : values) {
            result.add(FilterAccumulated.formatValue(Double.parseDouble(sValor)));
        }
        return result;
    }

    private double getDoubleValue() {
        return this.getDoubleValues().get(0);
    }

    public boolean verifyCondition(double originalValue) {
        boolean result = false;
        originalValue = FilterAccumulated.formatValue(originalValue);
        switch (operator.getSymbol()) {
            case Operators.EQUAL_TO -> {
                List<Double> valores = this.getDoubleValues();
                for (Double valor : valores) {
                    if (originalValue == valor) {
                        result = true;
                        break;
                    }
                }
            }
            case Operators.GREATER_THAN -> {
                if (originalValue > this.getDoubleValue()) {
                    result = true;
                }
            }
            case Operators.LESS_THAN -> {
                if (originalValue < this.getDoubleValue()) {
                    result = true;
                }
            }
            case Operators.GREATER_TAN_OR_EQUAL -> {
                if (originalValue >= this.getDoubleValue()) {
                    result = true;
                }
            }
            case Operators.LESS_THAN_OR_EQUAL -> {
                if (originalValue <= this.getDoubleValue()) {
                    result = true;
                }
            }
            case Operators.NOT_EQUAL_TO -> {
                List<Double> valores = this.getDoubleValues();
                for (Double valor : valores) {
                    if (originalValue == valor) {
                        result = false;
                        break;
                    }
                    result = true;
                }
            }
        }
        return result;
    }


    public String getFormattedValue() {

        StringBuilder result = new StringBuilder();
        List<String> values = BIUtil.stringToList(this.value, ";");
        for (String value : values) {
            result.append(BIUtil.formatDoubleToText(value, 2));
        }
        if (!values.isEmpty()) {
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }
        return result.toString();

    }

    public String getFieldName() {
        return this.field.getName();
    }

    public String getFieldTitle() {
        return "Acum(" + this.field.getTitle() + ")";
    }

    public Object clone() {
        Object retorno = null;
        try {
            retorno = super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("FilterAccumulated.clone: Não foi possível clonar o objeto FiltroAcumulado");
        }
        return retorno;
    }

    public String toString() {
        return "#$Acum(" + this.field.getFieldId() + ")$!" + " " + this.operator.getSymbol() + " " + this.value;
    }

    public static double formatValue(double valor) {
        return BIUtil.formatDoubleValue(valor, 2);
    }
}
