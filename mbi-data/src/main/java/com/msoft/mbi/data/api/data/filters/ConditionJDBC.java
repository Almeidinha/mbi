package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIPreparedStatementAction;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.extern.log4j.Log4j2;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log4j2
public class ConditionJDBC extends Condition implements Cloneable {

    public ConditionJDBC() {
        super();
    }

    public ConditionJDBC(Field field, Operator operator, String value) throws BIException {
        super(field, operator, value);
    }

    public ConditionJDBC(Condition condition) throws BIException {
        super(condition);
    }

    public ConditionJDBC(Field field, String operator, String value) throws BIException {
        this(field, new Operator(operator), value);
    }

    protected Object applyValues(Object stmt, int position) throws BIException {
        try {
            PreparedStatement statement = (PreparedStatement) stmt;
            int count = this.getValuesMap().size();
            for (int index = 1; index <= count; index++) {
                String valueAsString = String.valueOf(this.getValuesMap().get(index));
                Object formattedValue = this.format(valueAsString);
                this.apply(statement, position, formattedValue);
                position++;
            }
            return position;
        } catch (SQLException e) {
            throw new BISQLException(e, "Error applying value at position: " + position);
        }
    }

    private void apply(PreparedStatement stmt, int position, Object valor) throws SQLException {
        BIPreparedStatementAction action = new BIPreparedStatementAction(stmt);
        String type = this.getField().getDataType();
        if (Constants.NUMBER.equals(type)) {
            action.setDouble(position, (Double) valor);
        } else if (Constants.STRING.equals(type)) {
            action.setString(position, String.valueOf(valor));
        } else {
            action.setDate(position, (Date) valor);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ConditionJDBC conditionJDBC;
        try {
            conditionJDBC = new ConditionJDBC(this);
        } catch (BIException e) {
            log.error("Error in ConditionJDBC.clone() : " + e.getMessage());
            return null;
        }
        return conditionJDBC;
    }

    protected Object format(String valor) throws BIException {
        return BIUtil.formatSQL(this.getField(), valor);
    }

    @Override
    public String getFormattedValue() {
        try {
            return this.formatSQLValue();
        } catch (BIException e) {
            log.error("Error in ConditionJDBC.getFormattedValue() : " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
