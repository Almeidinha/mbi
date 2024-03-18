package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIPreparedStatementAction;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    protected Object applyValues(Object stmt, int posicao) throws BIException {
        try {
            PreparedStatement statement = (PreparedStatement) stmt;
            int count = this.getValuesMap().size();
            for (int index = 1; index <= count; index++) {
                String valor = String.valueOf(this.getValuesMap().get(index));
                Object oValor = this.format(valor);
                this.apply(statement, posicao, oValor);
                posicao++;
            }
            return posicao;
        } catch (SQLException e) {
            throw new BISQLException(e, "Erro ao aplicar valor na posicao: " + posicao);
        }
    }

    private void apply(PreparedStatement stmt, int posicao, Object valor) throws SQLException {
        BIPreparedStatementAction action = new BIPreparedStatementAction(stmt);
        String tipo = this.getField().getDataType();
        if (Constants.NUMBER.equals(tipo)) {
            action.setDouble(posicao, (Double) valor);
        } else if (Constants.STRING.equals(tipo)) {
            action.setString(posicao, String.valueOf(valor));
        } else {
            action.setDate(posicao, (Date) valor);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ConditionJDBC conditionJDBC;
        try {
            conditionJDBC = new ConditionJDBC(this);
        } catch (BIException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
