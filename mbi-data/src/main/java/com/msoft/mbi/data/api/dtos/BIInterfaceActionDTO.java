package com.msoft.mbi.data.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIInterfaceActionDTO implements Serializable {

    private Integer id;

    private int interfaceId;

    private int actionWeight;

    private String description;

    private BIInterfaceDTO biInterface;

    public BIInterfaceActionDTO(int id, int interfaceId, int actionWeight, String description) {
        this.id = id;
        this.interfaceId = interfaceId;
        this.actionWeight = actionWeight;
        this.description = description;
    }
}
