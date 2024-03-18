package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;


public interface Filter {

    public void setCondition(Condition condition);

    public void setCondition(Field field, Operator operator, String value) throws BIException;

    public void setCondition(Field field, String operator, String valor) throws BIException;

    public Condition getCondition();

    public String getFormattedValue() throws BIException;

    public Object applyValues(Object stmt, Integer position) throws BIException;

    public void setStartParentheses(boolean bool);

    public void setEndParentheses(boolean bool);

    public boolean haveStartParentheses();

    public boolean haveEndParentheses();

    public int getStartParentCount();

    public void setStartParentCount(int count);

    public int getEndParentCount();

    public void setEndParentCount(int count);

}
