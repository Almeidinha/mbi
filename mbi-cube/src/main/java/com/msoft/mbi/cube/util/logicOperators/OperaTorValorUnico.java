package com.msoft.mbi.cube.util.logicOperators;

import java.io.Serial;
import java.util.List;

public abstract class OperaTorValorUnico<T> implements OperaTor<T> {
	@Serial
	private static final long serialVersionUID = 6101801198611445903L;

	public T getPrimeiroValor(List<T> valoresComparar) {
		return valoresComparar.get(0);
	}

	public boolean compare(T valor1, List<T> valuesToCompare) {
		return this.compara(valor1, this.getPrimeiroValor(valuesToCompare));
	}

	public abstract boolean compara(T valor1, T valor2);
}
