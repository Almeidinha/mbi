package com.msoft.mbi.data.api.dtos.indicators;

import com.msoft.mbi.data.api.data.indicator.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartialTotalizationDTO {

    private Field field;
    private Object[][] values;

    private double partialTotalization;
    private int sequence;
}
