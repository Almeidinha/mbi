package com.msoft.mbi.data.api.dtos.user;

import com.msoft.mbi.data.api.dtos.BIAccessRestrictionInterfaceDTO;
import com.msoft.mbi.data.api.dtos.BaseDTO;
import com.msoft.mbi.model.BICompanyEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIUserDTO extends BaseDTO {

    private Integer id;

    private Integer userGroupId;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean isAdmin;

    private Boolean isActive;

    private Integer defaultPanel;

    private Integer biCompanyId;

    private BIUserGroupDTO userGroup;

    private List<BIUserInterfaceDTO> userInterface;

    private List<BIAccessRestrictionInterfaceDTO> accessRestrictionInterface;
}
