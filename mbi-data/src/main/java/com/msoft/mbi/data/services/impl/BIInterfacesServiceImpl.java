package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.BIInterfaceDTO;
import com.msoft.mbi.data.api.mapper.BIInterfaceMapper;
import com.msoft.mbi.data.repositories.BIInterfacesRepository;
import com.msoft.mbi.data.services.BIInterfacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BIInterfacesServiceImpl implements BIInterfacesService {

    private final BIInterfacesRepository biInterfacesRepository;
    private final BIInterfaceMapper biInterfaceMapper;

    @Override
    public BIInterfaceDTO loadInterface(Integer interfaceId) {
        return biInterfacesRepository.loadInterface(interfaceId);
    }

    @Override
    public List<BIInterfaceDTO> findAll() {
        return  this.biInterfacesRepository.findAll()
                .stream()
                .map(biInterfaceMapper::biInterfaceEntityToDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<BIInterfaceDTO> loadInterfaces() {
        return biInterfacesRepository.loadInterfaces();
    }
}
