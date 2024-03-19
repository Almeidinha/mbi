package com.msoft.mbi.cube.multi.metaData;

import java.util.Comparator;

public class OrdenacaoCampoComparator implements Comparator<MetaDataField> {

    @Override
    public int compare(MetaDataField campo1, MetaDataField campo2) {
        if (campo1.getOrder() > 0) {
            if (campo2.getOrder() > 0) {
                return campo1.getOrder().compareTo(campo2.getOrder());
            }
            return -1;
        } else {
            if (campo2.getOrder() > 0) {
                return 1;
            }
            return 0;
        }
    }
}
