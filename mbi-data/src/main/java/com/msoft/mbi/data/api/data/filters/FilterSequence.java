package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.indicator.Operators;
import com.msoft.mbi.data.api.data.util.BIUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FilterSequence implements FilterFunction, Cloneable {

    private Operator operator;
    private String value;
    private int amount;
    private int type;
    private boolean ranking;

    public FilterSequence(Operator operator, String valor) {
        this.setOperator(operator);
        this.setValue(valor);
        this.setType(FilterFunction.SEQUENCE_FILTER);
    }


    public void setOperator(Operator operator) {
        if (operator != null && (operator.getSymbol().equals(Operators.FIRST_N) || operator.getSymbol().equals(Operators.LAST_N))) {
            ranking = true;
        }
        this.operator = operator;
    }

    private List<Double> getDoubleValues() {
        List<Double> result = new ArrayList<>();
        List<String> values = BIUtil.stringtoList(this.value, ";");
        for (String sValor : values) {
            result.add(Double.parseDouble(sValor));
        }
        return result;
    }

    private double getValorDouble() {
        return this.getDoubleValues().get(0);
    }

    public boolean verifyCondition(double valorOriginal) {
        boolean retorno = false;
        if (operator.getSymbol().equals(Operators.LESS_THAN)) {
            if (valorOriginal < this.getValorDouble()) {
                retorno = true;
            }
        } else if (operator.getSymbol().equals(Operators.GREATER_THAN)) {
            if (valorOriginal > getValorDouble()) {
                retorno = true;
            }
        } else if (operator.getSymbol().equals(Operators.LESS_THAN_OR_EQUAL)) {
            if (valorOriginal <= getValorDouble()) {
                retorno = true;
            }
        } else if (operator.getSymbol().equals(Operators.GREATER_TAN_OR_EQUAL)) {
            if (valorOriginal >= getValorDouble()) {
                retorno = true;
            }
        } else if (operator.getSymbol().equals(Operators.EQUAL_TO)) {
            List<Double> values = this.getDoubleValues();
            for (Double valor : values) {
                if (valorOriginal == valor) {
                    retorno = true;
                    break;
                }
            }
        } else if (operator.getSymbol().equals(Operators.NOT_EQUAL_TO)) {
            List<Double> values = this.getDoubleValues();
            for (Double valor : values) {
                if (valorOriginal == valor) {
                    retorno = false;
                    break;
                }
                retorno = true;
            }
        }
        return retorno;
    }

    public boolean verifyRanking(double sequenciaAtual, int total) {
        boolean retorno = false;
        if (operator.getSymbol().equals(Operators.FIRST_N)) {
            if (sequenciaAtual <= getValorDouble()) {
                retorno = true;
            }
        } else if (operator.getSymbol().equals(Operators.LAST_N)) {
            double referenceValue = total - getValorDouble();
            if (sequenciaAtual > referenceValue) {
                retorno = true;
            }
        }
        return retorno;
    }

    public String getFieldName() {
        return "#$sequencia$!";
    }

    public String getFieldTitle() {
        return "sequencia";
    }

    public String getFormattedValue() {
        StringBuilder retorno = new StringBuilder();
        List<String> valores = BIUtil.stringtoList(this.value, ";");
        for (String sValor : valores) {
            retorno.append((int) (Double.parseDouble(sValor))).append(";");
            ;
        }
        if (!valores.isEmpty()) {
            retorno = new StringBuilder(retorno.substring(0, retorno.length() - 1));
        }
        return retorno.toString();
    }

    public Object clone() {
        Object retorno = null;
        try {
            retorno = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.out.println("Não foi possível clonar o objeto FiltroSequencia");
        }
        return retorno;
    }

    public String toString() {
        return this.getFieldName() + " " + this.getOperator().getSymbol() + " " + this.getValue();
    }

}
