package com.msoft.mbi.data.api.dtos;

import com.msoft.mbi.data.api.dtos.user.BIUserGroupInterfaceDTO;
import com.msoft.mbi.data.api.dtos.user.BIUserInterfaceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIInterfaceDTO implements Serializable {

    public BIInterfaceDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private Integer id;

    private String name;

    private List<BIInterfaceActionDTO> biInterfaceActions;

    private List<BIUserGroupInterfaceDTO> biUserGroupInterfaces;

    private List<BIAccessRestrictionInterfaceDTO> biAccessRestrictionInterfaces;

    private List<BIUserInterfaceDTO> biUserInterfaces;
}
