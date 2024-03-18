package com.msoft.mbi.data.api.data.indicator;

import lombok.*;


@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineColor implements Cloneable {

    private String initialValue;
    private String finalValue;
    private String colorClass;
    private String backGroundColor;
    private String fontColor;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
