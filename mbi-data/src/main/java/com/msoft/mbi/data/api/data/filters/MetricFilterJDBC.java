package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

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
        String inputValues = this.getCondition().getValue().replaceAll(";", ",");
        List<String> values = BIUtil.stringToList(inputValues, ",");

        return values.stream()
                .mapToDouble(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid number format in condition value: " + value);
                    }
                })
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(";"));
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
    protected Object clone() throws CloneNotSupportedException {
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
