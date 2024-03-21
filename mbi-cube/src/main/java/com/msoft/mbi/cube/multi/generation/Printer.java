package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public interface Printer {

    public void startPrinting();

    public void endPrinting();

    public String getNullValue();

    public String getEmptyValue();

    public void setDefaultBorderColor(String corBorda);

    public void openLine();

    public void closeLine();

    public void addStyle(CellProperty cellProperty, String name);

    public void addColumnHeaderStyle(CellProperty cellProperty, String name);

    public void addColumnSpecificPropertyStyle(CellProperty cellProperty, String name);

    public void addLinkStyle(CellProperty cellProperty, String name);

    public void printColumn(String cellProperty, String formattedValue);

    public void printColumn(String cellProperty, String formattedValue, int colspan, int rowspan);

    public void printColumnHeader(String cellProperty, ColumnMetaData metaData);

    public void printColumnHeader(String cellProperty, ColumnMetaData metaData, int colspan, int rowspan);

    public void printColumnHeader(String cellProperty, String title);

    public void printDimensionLineHeader(DimensionMetaData dimensionMetaData);

    public void printTotalPartialHeader(String cellProperty, String value, int colspan, int rowspan,
                                        DimensionMetaData dimensionMetaData);

    public void printColumnValue(String cellProperty, int colspan, int rowspan, Object valor,
                                 ColumnMetaData metaData);

    public void printColumnValue(String cellProperty, Object value, ColumnMetaData metaData);

    public void printDimensionLineValue(String cellProperty, int colspan, int rowspan, Object valor,
                                        DimensionMetaData metaData);

    public void printMetricValue(String cellProperty, Double valor, MetricMetaData metaData);

    public void printNumberValue(String cellProperty, Double valor, int decimalNumber);

    public void printSequenceField(String sequence);

    public void printSequenceField(DimensionMetaData dimensionMetaData, String sequence, int colspan, int rowspan);

    public void printPercentNumberValue(String name, Double value, int decimalNumber);

    void openHeadLine();

    void closeHeadLine();

    void openBodyLine();

    void closeBodyLine();

}
