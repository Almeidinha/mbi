package com.msoft.mbi.data.api.dtos.indicators;

import com.msoft.mbi.data.api.dtos.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BIIndInfoDTO extends BaseDTO {

    private Integer id;

    private int companyId;

    private int areaId;

    private String name;

    private String areaName;

    private List<BIUserIndDTO> biUserIndDtoList;

    private List<BIUserGroupIndDTO> biUserGroupIndDtoList;



}
