package com.msoft.mbi.cube.multi.metaData;

import java.util.Comparator;

public class OrdenacaoCampoComparator implements Comparator<CampoMetaData> {

    @Override
    public int compare(CampoMetaData campo1, CampoMetaData campo2) {
        if (campo1.getOrdem() > 0) {
            if (campo2.getOrdem() > 0) {
                return campo1.getOrdem().compareTo(campo2.getOrdem());
            }
            return -1;
        } else {
            if (campo2.getOrdem() > 0) {
                return 1;
            }
            return 0;
        }
    }
}
