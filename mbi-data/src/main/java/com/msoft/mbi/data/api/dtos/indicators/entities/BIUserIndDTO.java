package com.msoft.mbi.data.api.dtos.indicators.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BIUserIndDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;

    private Integer indicatorId;

    private boolean canChange;

    private boolean favorite;

}
