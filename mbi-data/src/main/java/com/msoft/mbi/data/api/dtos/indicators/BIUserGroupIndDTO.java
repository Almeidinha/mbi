package com.msoft.mbi.data.api.dtos.indicators;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BIUserGroupIndDTO {

    private Integer indicatorId;

    private Integer userGroupId;

    private boolean canEdit;

}
