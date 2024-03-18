package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MetricFilterJDBC extends MetricFilter {

    private boolean startParentheses = false;
    private boolean endParentheses = false;
    private int startParentCount = 0;
    private int endParentCount = 0;

    public MetricFilterJDBC() {
        super();
    }

    public MetricFilterJDBC(Condition condition) {
        super.setCondition(condition);
    }

    public String getFormattedValue() throws BIException {

        String valor = this.getCondition().getValue();
        valor = valor.replaceAll(";", ",");
        List<String> valores = BIUtil.stringtoList(valor, ",");
        StringBuilder retorno = new StringBuilder();
        for (String sValor : valores) {
            retorno.append(Double.parseDouble(sValor)).append(";");
        }
        if (!valores.isEmpty()) {
            retorno = new StringBuilder(retorno.substring(0, retorno.length() - 1));
        }
        return retorno.toString();
    }

    public void setCondition(Field field, Operator operator, String value) throws BIException {
        ConditionJDBC conditionJDBC = new ConditionJDBC(field, operator, value);
        super.setCondition(conditionJDBC);
    }


    @Override
    public void setCondition(Field field, String operator, String value) throws BIException {
        ConditionJDBC conditionJDBC = new ConditionJDBC(field, operator, value);
        super.setCondition(conditionJDBC);
    }

    @Override
    protected Object clone() {
        return new MetricFilterJDBC(this.getCondition());
    }

    @Override
    public boolean haveStartParentheses() {
        return this.startParentheses;
    }

    @Override
    public boolean haveEndParentheses() {
        return endParentheses;
    }

}
