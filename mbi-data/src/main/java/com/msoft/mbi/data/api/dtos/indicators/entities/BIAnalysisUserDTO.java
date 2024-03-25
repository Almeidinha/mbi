package com.msoft.mbi.data.api.dtos.indicators.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BIAnalysisUserDTO {

    private int indicatorId;

    private int userId;

    private Date lastAccessDate;

    private String lastAccessHour;

}
