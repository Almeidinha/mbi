package com.msoft.mbi.cube.multi.metadata;

import java.util.Comparator;

public class FiedldSequenceComparator implements Comparator<MetaDataField> {

    @Override
    public int compare(MetaDataField fieldA, MetaDataField fieldB) {
        return fieldA.getSequence().compareTo(fieldB.getSequence());
    }
}
