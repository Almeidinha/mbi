package com.msoft.mbi.data.api.dtos.user;

import com.msoft.mbi.data.api.dtos.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIUserGroupDTO extends BaseDTO {

    private Integer id;

    private String name;

    private String description;

    private String roleCode;

    private List<BIUserDTO> biUserDTOS;

    private List<BIUserGroupInterfaceDTO> biUserGroupInterfaceDTOS;
}
