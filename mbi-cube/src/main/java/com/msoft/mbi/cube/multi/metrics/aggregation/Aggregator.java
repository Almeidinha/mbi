package com.msoft.mbi.cube.multi.metrics.aggregation;


import lombok.Setter;

@Setter
public abstract class Aggregator {

    protected Double value;

    public Double getAggregatorValue() {
        return this.value;
    }

    public abstract void aggregatorValue(Double newValue);
}
