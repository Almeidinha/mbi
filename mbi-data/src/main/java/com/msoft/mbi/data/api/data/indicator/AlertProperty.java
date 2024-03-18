package com.msoft.mbi.data.api.data.indicator;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AlertProperty {

    private String fontName;
    private int fontSize;
    private String fontStyle;
    private String fontColor;
    private String cellBackgroundColor;

    public boolean hasBold() {
        return this.fontStyle != null && !this.fontStyle.isEmpty() && this.fontStyle.contains("B");
    }

    public boolean hasItalic() {
        return this.fontStyle != null && !this.fontStyle.isEmpty() && this.fontStyle.contains("I");
    }
}
