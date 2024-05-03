package com.msoft.mbi.data.api.dtos.indicators.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiAnalysisFieldIdDTO {

    private Integer fieldId;

    private Integer indicatorId;
}
