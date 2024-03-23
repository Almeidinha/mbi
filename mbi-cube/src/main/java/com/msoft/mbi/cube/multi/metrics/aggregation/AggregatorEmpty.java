package com.msoft.mbi.cube.multi.metrics.aggregation;


public class AggregatorEmpty extends Aggregator {

    @Override
    public void aggregatorValue(Double newValue) {
        if (this.value == null) {
            this.value = newValue;
        }
    }

}
