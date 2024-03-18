package com.msoft.mbi.cube.util.logicOperators;

import java.io.Serializable;
import java.util.List;

public interface OperaTor<T> extends Serializable {

  boolean compare(T valor1, List<T> valuesToCompare);
}
