package com.msoft.mbi.data.api.dtos.indicators;

import com.msoft.mbi.data.api.dtos.BaseDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BIUserGroupIndDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BIUserIndDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BIIndInfoDTO extends BaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public BIIndInfoDTO(Integer id, int companyId, String name, Integer areaId, String areaName) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.areaId = areaId;
        this.areaName = areaName;
    }

    private Integer id;

    private int companyId;

    private int areaId;

    private String name;

    private String areaName;

    private List<BIUserIndDTO> biUserIndDtoList;

    private List<BIUserGroupIndDTO> biUserGroupIndDtoList;

}
