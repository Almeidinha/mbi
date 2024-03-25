package com.msoft.mbi.data.api.data.indicator;

import java.util.ArrayList;
import java.util.Arrays;

public class PartialTotalizations {

    private final ArrayList<PartialTotalization> totalizationList;

    public PartialTotalizations() {
        this.totalizationList = new ArrayList<>();
    }

    public void addToTotalPartial(PartialTotalization totalPartial) {
        totalizationList.add(totalPartial);
    }

    public void removeFromTotalPartial(int index) {
        totalizationList.remove(index);
    }

    public void removeFromTotalPartial(PartialTotalization totalPartial) {
        totalizationList.remove(totalPartial);
    }

    public PartialTotalization getTotalPartial(Object[][] values, Field field) {
        for (PartialTotalization totalization : totalizationList) {
            Object[][] totalValues = totalization.getValues();
            if (totalValues.length == values.length && field.equals(totalization.getField())) {
                int matchCount = 0;
                for (int x = 0; x < totalValues.length; x++) {
                    if (Arrays.deepEquals(values[x], totalValues[x])) {
                        matchCount++;
                    } else {
                        break;
                    }
                }
                if (matchCount == totalValues.length) {
                    return totalization;
                }
            }
        }
        return null;
    }

    public double getTotalPartialAccumulated(Field field, int sequence) {
        return totalizationList.stream()
                .filter(totalization -> totalization.getField().equals(field) &&
                        totalization.getSequence() == sequence)
                .mapToDouble(PartialTotalization::getPartialTotalization)
                .sum();
    }
}
