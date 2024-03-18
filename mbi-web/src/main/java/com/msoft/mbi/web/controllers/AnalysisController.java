package com.msoft.mbi.web.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.data.api.data.inputs.AnalysisInput;
import com.msoft.mbi.data.api.dtos.indicators.BIIndLogicDTO;
import com.msoft.mbi.data.services.AnalysisService;
import com.msoft.mbi.data.services.BIIndService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final BIIndService indService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BIIndLogicDTO> createAnalysis(@RequestBody AnalysisInput analysisInput) {
        BIIndLogicDTO result = analysisService.createAnalysis(analysisInput, analysisInput.getConnectionId());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIIndLogicDTO> editAnalysis(@PathVariable Integer id, @RequestBody AnalysisInput analysisInput) {
        BIIndLogicDTO biIndEntity = this.analysisService.updateAnalysis(analysisInput, id);

        if (biIndEntity != null) {
            return ResponseEntity.ok(biIndEntity);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIIndLogicDTO> getAnalysis(@PathVariable Integer id) {
        BIIndLogicDTO biIndEntity = this.indService.getBIIndLogicDTO(id);

        if (biIndEntity != null) {
            return ResponseEntity.ok(biIndEntity);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}/table")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ObjectNode> getJsonTable(@PathVariable Integer id, @RequestBody(required = false) BIIndLogicDTO dto) {
        dto = Optional.ofNullable(dto).orElse(this.indService.getBIIndLogicDTO(id));

        ObjectNode table = this.analysisService.getTableAsJson(dto);

        if (table != null) {
            return ResponseEntity.ok(table);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{id}/table")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ObjectNode> getJsonTableFromInd(@PathVariable Integer id, @RequestBody(required = false) BIIndLogicDTO dto) {
        dto = Optional.ofNullable(dto).orElse(this.indService.getBIIndLogicDTO(id));

        ObjectNode table = this.analysisService.getTableAsJson(dto);

        if (table != null) {
            return ResponseEntity.ok(table);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
