package com.msoft.mbi.cube.multi.metaData;

import java.util.Comparator;

public class SequenciaCampoComparator implements Comparator<CampoMetaData> {

    @Override
    public int compare(CampoMetaData campo1, CampoMetaData campo2) {
        return campo1.getSequencia().compareTo(campo2.getSequencia());
    }
}
