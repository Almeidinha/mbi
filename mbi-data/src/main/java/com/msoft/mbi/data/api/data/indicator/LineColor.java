package com.msoft.mbi.data.api.data.indicator;

import lombok.*;


@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineColor {

    private String initialValue;
    private String finalValue;
    private String colorClass;
    private String backGroundColor;
    private String fontColor;

    public static LineColor copy(LineColor template) {
        LineColor copyLineColor = new LineColor();
        copyLineColor.setInitialValue(template.getInitialValue());
        copyLineColor.setFinalValue(template.getFinalValue());
        copyLineColor.setColorClass(template.getColorClass());
        copyLineColor.setBackGroundColor(template.getBackGroundColor());
        copyLineColor.setFontColor(template.getFontColor());

        return copyLineColor;
    }

}
