package com.msoft.mbi.cube.multi.renderers;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CellProperty implements Serializable {

    private int fontSize;
    private String fontName;
    private String fontColor;
    private String backGroundColor;
    private String alignment;
    private String borderColor;
    private boolean bold;
    private boolean italic;
    private boolean specificBorder = false;
    private int width;
    private String sFloat = null;
    private String dateMask = "";
    @Builder.Default
    private Map<String, String> extraAttributes = new HashMap<>();
    public static String ALIGNMENT_LEFT = "left";
    public static String ALIGNMENT_RIGHT = "right";
    public static String ALIGNMENT_CENTER = "center";
    public final static String CELL_PROPERTY_TOTAL_PARTIAL_LINES = "vlrTPcl";
    public final static String CELL_PROPERTY_TOTAL_PARTIAL_HEADER = "cbcTPcl";
    public final static String CELL_PROPERTY_COLUMN_TOTAL_HEADER = "cbcTCls";
    public final static String CELL_PROPERTY_TOTAL_GENERAL = "totGrl";
    public final static String CELL_PROPERTY_DIMENSION_HEADER = "cbcDim";
    public final static String PROPERTY_TEXT = "Texto";
    public final static String CELL_PROPERTY_DIMENSION_VALUE = "vlrDim";
    public final static String CELL_PROPERTY_METRIC_HEADER = "cbcMet";
    public final static String CELL_PROPERTY_OTHERS = "linOtrs";
    public final static String CELL_PROPERTY_SEQUENCE = "celSeq";
    public final static String CELL_PROPERTY_PREFIX = "propCel";
    public final static String CELL_PROPERTY_METRIC_VALUE_ONE = "vlrMet1";
    public final static String CELL_PROPERTY_METRIC_VALUE_TWO = "vlrMet2";
    public final static String CELL_PROPERTY_METRIC_DATA_ONE = "datMet1";
    public final static String CELL_PROPERTY_METRIC_DATA_TWO = "datMet2";
    public final static String CELL_PROPERTY_ALERTS_PREFIX = "alrt";
    public final static String[] CELL_PROPERTY_METRIC_VALUES = new String[]{CELL_PROPERTY_METRIC_VALUE_ONE, CELL_PROPERTY_METRIC_VALUE_TWO};
    public final static String CELL_PROPERTY_DEFAULT_HEADER = "cbcPad";
    public final static String CELL_PROPERTY_SEQUENCE_HEADER = "cbcPad";

    public String getSFloat() {
        return sFloat;
    }

    public void setSFloat(String float1) {
        sFloat = float1;
    }

    public void addExtraAttributes(String attribute, String value) {
        this.extraAttributes.put(attribute, value);
    }

}
