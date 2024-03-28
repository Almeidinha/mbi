package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.consult.ConsultResult;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class DimensionTotalized extends Dimension {

    @Getter
    @Setter
    private Object[][] results;
    @Setter
    @Getter
    private List<DimensionTotalized> childDimensions;
    private boolean isParent;
    @Getter
    private int resultNumber = 0;

    public PartialTotalization getPartialTotalization(Field field, Dimension dimension) {
        Object[][] values = dimension.consult(this.results);
        return this.getPartialTotalizations().getTotalPartial(values, field);

    }

    public boolean isRootDimension() {
        return this.getParentDimension() == null || this.getParentDimension().getValue() == null;
    }

    public DimensionTotalized(ConsultResult value, int size) {
        super(value, size, null, null);
    }

    public void setPartialTotalization(Field field, double value, Dimension dimension) {
        Object[][] values = dimension.consult(this.results);
        PartialTotalization partialTotalization = new PartialTotalization();
        partialTotalization.setField(field);
        partialTotalization.setPartialTotalization(value);
        partialTotalization.setValues(values);
        this.getPartialTotalizations().addToTotalPartial(partialTotalization);
    }

    public double getParentTotalization(Field field, Dimension dimension) {
        Object[][] values = dimension.consult(this.results);
        double totalValue = 0;
        for (DimensionTotalized childDimension : childDimensions) {
            PartialTotalization partialTotalization = childDimension.getPartialTotalization(field, dimension);
            totalValue += partialTotalization.getPartialTotalization();
        }
        PartialTotalization partialTotalization = new PartialTotalization();
        partialTotalization.setField(field);
        partialTotalization.setPartialTotalization(totalValue);
        partialTotalization.setValues(values);
        this.getPartialTotalizations().addToTotalPartial(partialTotalization);
        return totalValue;
    }


    public void addChild(DimensionTotalized child) {
        if (this.childDimensions == null)
            this.childDimensions = new ArrayList<>();
        this.childDimensions.add(child);
        this.resultNumber += child.getResults().length - 1;
    }


    @Override
    public void resizeResults() {
        if (this.results != null) {
            super.resizeResults(this.results);
        }
    }


    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean isParent) {
        this.isParent = isParent;
    }

}
