package com.msoft.mbi.data.api.dtos;

import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import com.msoft.mbi.model.BIUserGroupEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIAccessRestrictionInterfaceDTO {

    private int userGroupId;

    private int interfaceId;

    private int userId;

    private BIUserGroupEntity biUserGroupByUserGroup;

    private BIInterfaceDTO biInterfaceDTO;

    private BIUserDTO biUserDTO;
}
