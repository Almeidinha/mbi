package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIAnalysisFieldDTO;
import com.msoft.mbi.data.services.BIAnalysisFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/field")
@RequiredArgsConstructor
public class BIAnalysisFieldController {

    private final BIAnalysisFieldService fieldService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIAnalysisFieldDTO> getFieldDto(@PathVariable("id") Integer id) {
        BIAnalysisFieldDTO dto =  this.fieldService.findDtoById(id);

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BIAnalysisFieldDTO> create(@RequestBody BIAnalysisFieldDTO dto){
        return ResponseEntity.ok(this.fieldService.saveDto(dto));
    }

    @PutMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIAnalysisFieldDTO> updateField(@PathVariable Integer id, @RequestBody BIAnalysisFieldDTO dto){
        BIAnalysisFieldDTO updatedDto = fieldService.updateDto(id, dto);

        if (updatedDto != null) {
            return ResponseEntity.ok(updatedDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping({"/list"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIAnalysisFieldDTO>> updateFieldList(@RequestBody List<BIAnalysisFieldDTO> dtos){
        List<BIAnalysisFieldDTO> updatedDtos = fieldService.updateDtoList(dtos);

        if (updatedDtos != null && !updatedDtos.isEmpty()) {
            return ResponseEntity.ok(updatedDtos);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
