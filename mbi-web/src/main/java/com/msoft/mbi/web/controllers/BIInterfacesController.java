package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO;
import com.msoft.mbi.data.api.dtos.BIInterfaceDTO;
import com.msoft.mbi.data.services.BIInterfacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interfaces")
@RequiredArgsConstructor
public class BIInterfacesController {

    private final BIInterfacesService biInterfacesService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIInterfaceDTO>> getInterfaces() {
        List<BIInterfaceDTO> biInterfaceDTOS =  this.biInterfacesService.findAll();
        return ResponseEntity.ok(biInterfaceDTOS);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIInterfaceDTO> getInterfaceById(@PathVariable Integer id) {
        BIInterfaceDTO biInterfaceDTO =  this.biInterfacesService.loadInterface(id);
        return ResponseEntity.ok(biInterfaceDTO);
    }
}
