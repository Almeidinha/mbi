package com.msoft.mbi.cube.multi.dimension;

import java.util.TreeMap;

public class Dimensions extends TreeMap<Comparable<Dimension>, Dimension> {


    public Dimensions() {
        super();
    }

    public void addDimension(Dimension dimension) {
        put(dimension, dimension);
        dimension.setDimensionTotalizedLevelUp();
    }

    public synchronized void removeDimension(Dimension dimension) {
        Dimension.decreaseTotalSize(dimension);
        this.remove(dimension);
    }

    public void removeDimensions() {
        for (Dimension dimensionLine : this.values()) {
            Dimension.decreaseTotalSize(dimensionLine);
        }
        this.clear();
    }

}
