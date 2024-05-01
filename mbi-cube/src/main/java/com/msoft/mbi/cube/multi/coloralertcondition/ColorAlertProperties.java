package com.msoft.mbi.cube.multi.coloralertcondition;

import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class ColorAlertProperties extends CellProperty {


    public static ColorAlertProperties factory(String fontColor, String backGroundColor, String fontStyle, boolean bold, boolean italic, int fontSize) {
        ColorAlertProperties colorAlertProperties = new ColorAlertProperties();
        colorAlertProperties.setFontColor(fontColor);
        colorAlertProperties.setBackGroundColor(backGroundColor);
        colorAlertProperties.setFontName(fontStyle);
        colorAlertProperties.setBold(bold);
        colorAlertProperties.setItalic(italic);
        colorAlertProperties.setFontSize(fontSize);
        colorAlertProperties.addExtraAttributes("border", "1px solid #3377CC");
        colorAlertProperties.addExtraAttributes("border-top", "none");
        return colorAlertProperties;
    }

}
