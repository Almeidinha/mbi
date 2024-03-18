package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO;
import com.msoft.mbi.data.services.BIInterfaceActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interface")
@RequiredArgsConstructor
public class BIInterfaceActionController {

    private final BIInterfaceActionService biInterfaceActionService;

    @GetMapping("/actions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIInterfaceActionDTO>> getInterfaceActions(@PathVariable("id") Integer id) {
        List<BIInterfaceActionDTO> biInterfaceActionDTOS =  this.biInterfaceActionService.loadInterfaceActions(id);
        return ResponseEntity.ok(biInterfaceActionDTOS);
    }

}
