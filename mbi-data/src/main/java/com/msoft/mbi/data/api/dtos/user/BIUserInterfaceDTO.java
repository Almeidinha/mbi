package com.msoft.mbi.data.api.dtos.user;

import com.msoft.mbi.data.api.dtos.BIInterfaceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIUserInterfaceDTO {

    private int userId;

    private int interfaceId;

    private int permissionLevel;

    private BIUserDTO biUser;

    private BIInterfaceDTO biInterface;
}
