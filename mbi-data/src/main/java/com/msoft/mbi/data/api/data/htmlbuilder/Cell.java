package com.msoft.mbi.data.api.data.htmlbuilder;

public interface Cell {


    boolean isAppliedAlert();

    void setAppliedAlert(boolean appliedAlert);

    boolean isTHCell();

    boolean isDimensionColumn();

    void setDimensionColumn(boolean isDimensionColumn);

    boolean isExcel();

    void setStyle(Object style);

    Object getContent();

    void setCellClass(String cellClass);
}
