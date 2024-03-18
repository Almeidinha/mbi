package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO;

import java.util.List;

public interface BIInterfaceActionService {

    List<BIInterfaceActionDTO> loadInterfaceActions(Integer interfaceId);

}
