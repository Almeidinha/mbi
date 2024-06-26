package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public abstract class MetricFilter implements Filter {

    private Condition condition;

    public Field getField() {
        return this.condition.getField();
    }

    public Operator getOperator() {
        return this.condition.getOperator();
    }

    public String getSqlValue() {
        return this.condition.getSqlValue();
    }

    public String toString() {
        return Optional.ofNullable(this.condition)
                .map(Condition::getField)
                .map(field -> {
                    StringBuilder result = new StringBuilder();
                    String aggregation = field.getAggregationType();
                    if (field.isExpression()) {
                        result.append(this.condition.toString(true, true));
                    } else {
                        if (aggregation.isEmpty() || Constants.EMPTY.equals(aggregation)) {
                            aggregation = Constants.SUM;
                        }
                        result.append(aggregation).append(this.condition.toString(true, true));
                    }
                    return result.toString();
                })
                .orElse("");
    }

    public String toStringWithoutAggregation() {
        if (this.condition != null && this.condition.getField() != null) {
            return this.condition.toString(true, false);
        }
        return "";
    }

    public String toStringWithCode() throws BIException {
        String result = this.condition.toStringWithCode(true);
        return this.condition.applyValues(result, 0);
    }

    @Override
    public int applyValues(PreparedStatement stmt, Integer position) throws BIException {
        return this.getCondition().applyValues(stmt, position);
    }

    @Override
    public String applyValues(String query, Integer position) throws BIException {
        return this.getCondition().applyValues(query, position);
    }

    protected abstract MetricFilter copy() throws BIException;
}
