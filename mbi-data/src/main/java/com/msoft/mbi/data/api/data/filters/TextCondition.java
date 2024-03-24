package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIFilterException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Log4j2
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
    protected int applyValues(PreparedStatement stmt, int position) throws BIException {
        throw new NotImplementedException("TextCondition.applyValues(PreparedStatement stmt, int position) not available");
    }

    @Override
    protected String applyValues(String query, int position) throws BIException {

        for (Object mapValue : this.valuesMap.values()) {
            String value = String.valueOf(mapValue);
            query = query.replaceFirst("\\?", this.format(value));
        }

        return query;
    }

    protected Object clone() throws CloneNotSupportedException {
        TextCondition condition;
        try {
            condition = new TextCondition(this);
        } catch (BIException e) {
            log.error("Error inTextCondition.clone()" + e.getMessage());
            return null;
        }
        return condition;
    }

    protected String format(String valor) throws BIFilterException {
        return String.valueOf(BIUtil.textFormat(this.getField(), valor));
    }

    public String getFormattedValue() {
        try {
            if (getField() != null && Constants.DIMENSION.equals(getField().getDataType())) {
                final String format = "dd/MM/yy"; // TODO load from database

                String value = getValue().replaceAll(";", ",");
                List<String> values = BIUtil.stringToList(value, ",");

                if (!values.isEmpty()) {
                    return values.stream()
                            .map(v -> {
                                try {
                                    return BIUtil.getFormattedDate(v, format);
                                } catch (BIException e) {
                                    log.error("Error formatting date: " + v, e);
                                    return v;
                                }
                            })
                            .collect(Collectors.joining(";"));
                }
            }
        } catch (PatternSyntaxException e) {
            log.error("Invalid regex pattern in TextCondition.getFormattedValue(): " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("Null value encountered in TextCondition.getFormattedValue(): " + e.getMessage());
        }
        return getValue();
    }

    @Override
    public String toString(boolean isMetric, boolean doAggregate) {
        try {
            return this.applyValues(super.toString(isMetric, doAggregate), 0);
        } catch (BIException e) {
            log.error("Error inTextCondition.toString()" + e.getMessage());
            return null;
        }
    }

}
