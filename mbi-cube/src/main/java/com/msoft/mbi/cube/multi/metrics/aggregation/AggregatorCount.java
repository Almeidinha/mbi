package com.msoft.mbi.cube.multi.metrics.aggregation;


public class AggregatorCount extends Aggregator {


    @Override
    public void aggregatorValue(Double newValue) {
        if (this.value == null) {
            this.value = (double) 0;
        }
        this.value++;
    }

}
