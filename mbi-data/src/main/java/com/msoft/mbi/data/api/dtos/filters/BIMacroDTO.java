package com.msoft.mbi.data.api.dtos.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIMacroDTO {

    private String id;
    private String description;
    private String fieldType;
    private int incrementalField;
}
