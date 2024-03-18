package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.dtos.BIInterfaceDTO;

import java.util.List;

public interface BIInterfacesService {

    List<BIInterfaceDTO> loadInterfaces();

    BIInterfaceDTO loadInterface(Integer interfaceId);

    List<BIInterfaceDTO> findAll();

}
