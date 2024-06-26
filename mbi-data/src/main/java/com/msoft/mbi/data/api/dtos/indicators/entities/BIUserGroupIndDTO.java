package com.msoft.mbi.data.api.dtos.indicators.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BIUserGroupIndDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer indicatorId;

    private Integer userGroupId;

    private boolean canEdit;

}
