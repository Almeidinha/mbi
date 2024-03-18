package com.msoft.mbi.data.api.data.indicator;

import java.util.Comparator;

public class DrillDownComparator implements Comparator<Field> {

    public int compare(Field campo1, Field campo2) {
        return (Integer.compare(campo1.getDrillDownSequence(), campo2.getDrillDownSequence()));
    }

}
