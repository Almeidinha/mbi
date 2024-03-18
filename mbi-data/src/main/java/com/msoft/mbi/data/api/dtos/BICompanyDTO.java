package com.msoft.mbi.data.api.dtos;

import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BICompanyDTO extends BaseDTO {

    private Integer id;

    private String name;

    private boolean active;

    private String phone;

    private String address;

    private List<BIUserDTO> biUsers;

}
