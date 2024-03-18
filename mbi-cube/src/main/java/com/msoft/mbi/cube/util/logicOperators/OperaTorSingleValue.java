package com.msoft.mbi.cube.util.logicOperators;

import java.util.List;

public abstract class OperaTorSingleValue<T> implements OperaTor<T> {

	public T getFirstValue(List<T> valuesToCompare) {
		return valuesToCompare.get(0);
	}

	public boolean compare(T firstValue, List<T> valuesToCompare) {
		return this.compare(firstValue, this.getFirstValue(valuesToCompare));
	}

	public abstract boolean compare(T firstValue, T secondValue);
}
