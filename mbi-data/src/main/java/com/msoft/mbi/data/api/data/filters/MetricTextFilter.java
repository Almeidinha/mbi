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
        if (metricFilter != null && (metricFilter.getCondition() != null)) {
                this.setCondition(new TextCondition(metricFilter.getCondition()));

        }
    }

    public String getFormattedValue() throws BIException {
        Field field = this.getCondition().getField();
        String sqlValue = this.applyValues(this.getCondition().getSqlValue(), 0);
        List<String> values = BIUtil.stringToList(sqlValue, ",");

        return values.stream()
                .map(sValue -> {
                    try {
                        return String.valueOf(BIUtil.textFormat(field, sValue));
                    } catch (Exception e) {
                        throw new RuntimeException("Error formatting value: " + sValue, e);
                    }
                })
                .collect(Collectors.joining(";"));
    }

    @Override
    public String applyValues(String query, Integer position) throws BIException {
        return super.applyValues(query, position);
    }

    @Override
    protected MetricFilter copy() throws BIException {
        return new MetricTextFilter(this.getCondition());
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
