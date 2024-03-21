package com.msoft.mbi.cube.multi.dimension.comparator;

import com.msoft.mbi.cube.multi.dimension.Dimension;

public class DimensaoPadraoComparator extends DimensaoComparator {


    private static DimensaoPadraoComparator padraoComparator;

    private DimensaoPadraoComparator() {
        super();
    }

    public static final DimensaoPadraoComparator getInstance() {
        if (padraoComparator == null) {
            padraoComparator = new DimensaoPadraoComparator();
        }
        return padraoComparator;
    }

    @Override
    public int compare(Comparable<Dimension> o1, Comparable<Dimension> o2) {
        Dimension dimension1 = (Dimension) o1;
        Dimension dimension2 = (Dimension) o2;
        return dimension1.getOrderValue().compareTo(dimension2.getOrderValue()) * dimension1.getMetaData().getOrderingType();
    }
}
