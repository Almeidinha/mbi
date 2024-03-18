package com.msoft.mbi.data.api.dtos.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BIUserScheduleDTO {

    private int scheduleId;

    private int userId;
}
