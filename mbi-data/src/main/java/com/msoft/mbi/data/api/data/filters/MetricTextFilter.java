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
public class MetricTextFilter extends MetricFilter {

    private boolean startParentheses = false;
    private boolean endParentheses = false;
    private int startParentCount = 0;
    private int endParentCount = 0;

    public MetricTextFilter() {
        super();
    }

    public MetricTextFilter(Condition condition) {
        super.setCondition(condition);
    }

    public MetricTextFilter(MetricFilter metricFilter) throws BIException {
        if (metricFilter != null) {
            if (metricFilter.getCondition() != null) {
                this.setCondition(new TextCondition(metricFilter.getCondition()));
            }
        }
    }

    public MetricTextFilter(MetricSqlFilter metricFilters) throws BIException {
        if (metricFilters != null) {
            return;
        }
    }

    public String getFormattedValue() throws BIException {
        Field field = this.getCondition().getField();

        String valorSQL = String.valueOf(this.applyValues(this.getCondition().getSQLValue(), 0));
        List<String> values = BIUtil.stringToList(valorSQL, ",");
        StringBuilder retorno = new StringBuilder();
        for (String sValor : values) {
            retorno.append(BIUtil.textFormat(field, sValor)).append(";");
        }
        if (!values.isEmpty()) {
            retorno = new StringBuilder(retorno.substring(0, retorno.length() - 1));
        }
        return retorno.toString();
    }

    public void setCondition(Field field, Operator operator, String value) throws BIException {
        TextCondition condition = new TextCondition(field, operator, value);
        super.setCondition(condition);
    }

    public void setCondition(Field campo, String operator, String value) throws BIException {
        TextCondition condition = new TextCondition(campo, operator, value);
        super.setCondition(condition);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new MetricTextFilter(this.getCondition());
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean haveStartParentheses() {
        return this.startParentheses;
    }

    @Override
    public boolean haveEndParentheses() {
        return this.endParentheses;
    }
}
