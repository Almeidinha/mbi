package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIFilterException;
import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.util.BIUtil;

import java.util.List;


public class TextCondition extends Condition {

    public TextCondition() {
    }

    public TextCondition(Condition condition) throws BIException {
        super(condition);
    }

    public TextCondition(Field field, Operator operator, String value) throws BIException {
        super(field, operator, value);
    }

    public TextCondition(Field field, String operator, String value) throws BIException {
        this(field, new Operator(operator), value);
    }

    @Override
    protected Object applyValues(Object stmt, int position) throws BIException {
        StringBuilder queryBuilder = new StringBuilder((String) stmt);
        int count = this.valuesMap.size();
        for (int index = 1; index <= count; index++) {
            String valor = (String) this.getValuesMap().get(index);
            Object oValor = this.format(valor);
            int ind = queryBuilder.indexOf("?");
            if (ind != -1) {
                queryBuilder.deleteCharAt(ind);
                queryBuilder.insert(ind, oValor.toString());
            }
        }
        return queryBuilder.toString();
    }

    protected Object clone() throws CloneNotSupportedException {
        TextCondition condition;
        try {
            condition = new TextCondition(this);
        } catch (BIException e) {
            e.printStackTrace();
            return null;
        }
        return condition;
    }

    protected Object format(String valor) throws BIFilterException {
        return BIUtil.textFormat(this.getField(), valor);
    }

    public String getFormattedValue() {
        try {
            if (this.getField() != null && "D".equals(this.getField().getDataType())) {
                String format = "dd/MM/yy"; // TODO GET THIS FROM DATABASE
                String valor = this.getValue();
                valor = valor.replaceAll(";", ",");
                List<String> values = BIUtil.stringtoList(valor, ",");

                StringBuilder retorno = new StringBuilder();
                for (String v : values) {
                    retorno.append(BIUtil.getFormattedDate(v, format)).append(";");
                }
                if (!values.isEmpty()) {
                    retorno = new StringBuilder(retorno.substring(0, retorno.length() - 1));
                }
                return retorno.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getValue();
    }

    @Override
    public String toString(boolean isMetric, boolean agrega) {
        try {
            return (String) this.applyValues(super.toString(isMetric, agrega), 0);
        } catch (BIException e) {
            e.printStackTrace();
            return null;
        }
    }

}
