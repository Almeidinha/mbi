package com.msoft.mbi.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class BIUserGroupIndicatorPK implements Serializable {

    private Integer indicatorId;

    private Integer userGroupId;

}
