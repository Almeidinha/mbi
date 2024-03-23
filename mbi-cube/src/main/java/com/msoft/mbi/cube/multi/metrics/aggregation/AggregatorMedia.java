package com.msoft.mbi.cube.multi.metrics.aggregation;

public class AggregatorMedia extends Aggregator {


    private Double sum;
    private int valuesCount = 0;

    @Override
    public void aggregatorValue(Double newValue) {
        if (this.sum == null) {
            this.sum = (double) 0;
        }
        this.sum += newValue;
        this.valuesCount++;
        this.value = this.sum / this.valuesCount;
    }

}
