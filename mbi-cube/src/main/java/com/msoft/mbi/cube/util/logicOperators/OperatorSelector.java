package com.msoft.mbi.cube.util.logicOperators;

public interface OperatorSelector<T> {

  OperaTor<T> getOperator(String operator);
}
