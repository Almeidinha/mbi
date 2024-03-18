package com.msoft.mbi.data.api.dtos.user;

import com.msoft.mbi.data.api.dtos.BaseDTO;
import com.msoft.mbi.data.api.dtos.user.BIUserGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserDTO extends BaseDTO {

    private Integer id;

    private Integer userGroupId;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean isAdmin;

    private Boolean isActive;

    private Integer defaultPanel;

    private BIUserGroupDTO userGroup;

}
