package com.msoft.mbi.data.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIAreaDTO extends BaseDTO {
    private Integer id;
    int companyId;
    String description;
}
