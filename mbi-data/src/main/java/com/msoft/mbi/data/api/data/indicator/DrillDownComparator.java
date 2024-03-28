package com.msoft.mbi.data.api.data.indicator;

import java.util.Comparator;

public class DrillDownComparator implements Comparator<Field> {

    public int compare(Field fieldOne, Field fieldTwo) {
        return (Integer.compare(fieldOne.getDrillDownSequence(), fieldTwo.getDrillDownSequence()));
    }

}
