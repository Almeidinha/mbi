package com.msoft.mbi.data.api.dtos.indicators;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BIUserIndDTO {

    private Integer userId;

    private Integer indicatorId;

    private boolean canChange;

    private boolean favorite;

}
