package com.msoft.mbi.data.api.dtos.indicators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertPropertyDTO {

    private String fontName;
    private int fontSize;
    private String fontStyle;
    private String fontColor;
    private String cellBackgroundColor;
}
