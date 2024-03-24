package com.msoft.mbi.cube.multi.column;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaskColumnMetaData {

    private String mascara;
    private int type;
    public static final int TYPE_BEFORE = 1;
    public static final int TYPE_AFTER = 2;
    public static final int DATA_TYPE = 3;
    public static final int TYPE_EIS_DIMENSION_MONTH = 4;
    public static final int TYPE_EIS_DIMENSION_MONTH_YEAR = 5;
    public static final int TYPE_EIS_DIMENSION_PERIOD = 6;
    public static final int TYPE_EIS_DIMENSION_WEEK = 7;

    public MaskColumnMetaData(String mascara, int type) {
        this.mascara = mascara;
        this.type = type;
    }

}
