package com.msoft.mbi.cube.multi.dimension.comparator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public abstract class DimensaoComparator implements Comparator<Comparable<Dimension>>, Serializable {

	@Serial
	private static final long serialVersionUID = 3855375529249340929L;

	@Override
	public abstract int compare(Comparable<Dimension> o1, Comparable<Dimension> o2);

}
