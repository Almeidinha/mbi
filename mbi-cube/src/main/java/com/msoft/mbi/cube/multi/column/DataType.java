package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.OperatorSelector;

public interface DataType<T> extends OperatorSelector<T> {

    T getValor(ResultSet set, String campo) throws SQLException;

    Object format(T t);

    OperaTor<T> getOperator(String operator);
}
