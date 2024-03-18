package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO;
import com.msoft.mbi.data.repositories.BIInterfaceActionRepository;
import com.msoft.mbi.data.services.BIInterfaceActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BIInterfaceActionServiceImpl implements BIInterfaceActionService {

    private final BIInterfaceActionRepository biInterfaceActionRepository;
    @Override
    public List<BIInterfaceActionDTO> loadInterfaceActions(Integer interfaceId) {
        return biInterfaceActionRepository.loadInterfaceActions(interfaceId);
    }
}
