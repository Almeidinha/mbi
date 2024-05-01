package com.msoft.mbi.cube.multi.metadata;

import java.util.Comparator;

public class FieldOrderingComparator implements Comparator<MetaDataField> {

    @Override
    public int compare(MetaDataField fieldA, MetaDataField fieldB) {
        int orderA = fieldA.getOrder();
        int orderB = fieldB.getOrder();

        if (orderA > 0 && orderB > 0) {
            return Integer.compare(orderA, orderB);
        } else if (orderA > 0) {
            return 1;
        } else if (orderB > 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
