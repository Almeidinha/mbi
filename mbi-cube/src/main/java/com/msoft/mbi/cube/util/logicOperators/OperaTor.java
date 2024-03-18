package com.msoft.mbi.cube.util.logicOperators;

import java.util.List;

public interface OperaTor<T> {

  boolean compare(T firstValue, List<T> valuesToCompare);
}
