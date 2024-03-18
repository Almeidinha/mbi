package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.indicator.Operators;
import com.msoft.mbi.data.api.data.util.BIUtil;
import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        List<String> values = BIUtil.stringtoList(this.value, ";");
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
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        StringBuilder retorno = new StringBuilder();
        List<String> values = BIUtil.stringtoList(this.value, ";");
        for (String sValor : values) {
            retorno.append(nf.format(Double.parseDouble(sValor))).append(";");
            ;
        }
        if (!values.isEmpty()) {
            retorno = new StringBuilder(retorno.substring(0, retorno.length() - 1));
        }
        return retorno.toString();

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
            e.printStackTrace();
            System.out.println("Não foi possível clonar o objeto FiltroAcumulado");
        }
        return retorno;
    }

    public String toString() {
        return "#$Acum(" + this.field.getCode() + ")$!" + " " + this.operator.getSymbol() + " " + this.value;
    }

    public static double formatValue(double valor) {
        double retorno = 0;

        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);

        String valorString = nf.format(valor);
        valorString = valorString.replace('.', 'X');
        valorString = valorString.replaceAll("X", "");
        valorString = valorString.replaceAll(",", ".");
        retorno = Double.parseDouble(valorString);
        return retorno;
    }
}
