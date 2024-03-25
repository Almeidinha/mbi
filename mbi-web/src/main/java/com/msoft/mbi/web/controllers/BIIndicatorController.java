package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.indicators.BIIndInfoDTO;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.services.BIIndService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/indicator")
@RequiredArgsConstructor
public class BIIndicatorController {

    private final BIIndService indService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIIndInfoDTO>> getIndInfoDtoList() {
        return ResponseEntity.ok(this.indService.findAllDTOs());
    }

    @GetMapping("/dto/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<IndicatorDTO>> getIndLogicDTOList() {
        return ResponseEntity.ok(this.indService.findAllBIIndLogicDTOs());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIIndInfoDTO> getIndInfoDto(@PathVariable Integer id) {
        BIIndInfoDTO dto = this.indService.getBIIndDTO(id);
        if (dto == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/dto/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<IndicatorDTO> getIndLogicDto(@PathVariable Integer id) {
        return ResponseEntity.ok(this.indService.getBIIndLogicDTO(id));
    }

    @GetMapping("/{id}/sequence/{hasSequence}")
    @ResponseStatus(HttpStatus.OK)
    public void changeSequence(@PathVariable Integer id, @PathVariable boolean hasSequence) {
        this.indService.changeSequence(id, hasSequence);
    }
}
