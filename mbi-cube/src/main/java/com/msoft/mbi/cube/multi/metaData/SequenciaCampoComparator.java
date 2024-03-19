package com.msoft.mbi.cube.multi.metaData;

import java.util.Comparator;

public class SequenciaCampoComparator implements Comparator<MetaDataField> {

    @Override
    public int compare(MetaDataField campo1, MetaDataField campo2) {
        return campo1.getSequence().compareTo(campo2.getSequence());
    }
}
