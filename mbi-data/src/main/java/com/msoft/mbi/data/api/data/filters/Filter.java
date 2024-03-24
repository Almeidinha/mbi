package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;

import java.sql.PreparedStatement;


public interface Filter {

    void setCondition(Condition condition);

    void setCondition(Field field, Operator operator, String value) throws BIException;

    void setCondition(Field field, String operator, String valor) throws BIException;

    Condition getCondition();

    String getFormattedValue() throws BIException;

    int applyValues(PreparedStatement stmt, Integer position) throws BIException;

    String applyValues(String query, Integer position) throws BIException;

    void setStartParentheses(boolean bool);

    void setEndParentheses(boolean bool);

    boolean haveStartParentheses();

    boolean haveEndParentheses();

    int getStartParentCount();

    void setStartParentCount(int count);

    int getEndParentCount();

    void setEndParentCount(int count);

}
