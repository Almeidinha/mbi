package com.msoft.mbi.data.api.data.htmlbuilder;

import java.util.List;

public interface Line {

    void setStyle(Object style, boolean isDimension);

    List<Cell> getCells();

    boolean isAppliedAlert();

    void setAppliedAlert(boolean appliedAlert);

    Object getAppliedStyle();

    void setAppliedStyle(Object style);
}
