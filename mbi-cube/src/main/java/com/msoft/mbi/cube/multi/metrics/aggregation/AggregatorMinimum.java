package com.msoft.mbi.cube.multi.metrics.aggregation;


public class AggregatorMinimum extends Aggregator {


    @Override
    public void aggregatorValue(Double newValue) {
        if (this.value == null) {
            this.value = newValue;
        } else {
            if (newValue < this.value) {
                this.value = newValue;
            }
        }
    }

}
