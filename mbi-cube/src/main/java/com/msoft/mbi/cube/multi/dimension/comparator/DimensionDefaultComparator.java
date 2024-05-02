package com.msoft.mbi.cube.multi.dimension.comparator;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public class DimensionDefaultComparator extends DimensionComparator {

    private DimensionDefaultComparator() {
        super();
    }

    private static class SingletonHolder {
        private static final DimensionDefaultComparator INSTANCE = new DimensionDefaultComparator();
    }

    public static DimensionDefaultComparator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public int compare(Comparable<Dimension> o1, Comparable<Dimension> o2) {
        Dimension dimension1 = (Dimension) o1;
        Dimension dimension2 = (Dimension) o2;
        return dimension1.getOrderValue().compareTo(dimension2.getOrderValue()) * dimension1.getMetaData().getOrderingType();
    }
}
