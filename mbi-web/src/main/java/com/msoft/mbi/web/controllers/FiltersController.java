package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.inputs.FilterBuilderInput;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.services.FiltersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/v1/filters")
@RequiredArgsConstructor
public class FiltersController {

    private final FiltersService filtersService;

    @GetMapping("/{indicatorId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FiltersDTO> getFilters(@PathVariable Integer indicatorId) throws BIException {
        FiltersDTO filtersDTO =  this.filtersService.getFiltersDTO(indicatorId);
        return ResponseEntity.ok(filtersDTO);
    }

    @PostMapping("/{indicatorId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FiltersDTO> getFiltersFromInd(@PathVariable Integer indicatorId, @RequestBody(required = false) IndicatorDTO dto) throws BIException {
        FiltersDTO filtersDTO =  this.filtersService.getFiltersDTOFromDTO(indicatorId, dto);
        return ResponseEntity.ok(filtersDTO);
    }

    @PostMapping("/{indicatorId}/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FiltersDTO> updateIndFilters(@PathVariable Integer indicatorId, @RequestBody FiltersDTO dto) {
        this.filtersService.updateIndFilters(indicatorId, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = "/build", produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FiltersDTO> updateFilters(@RequestBody FilterBuilderInput filterBuilderInput) {
        try {
            FiltersDTO filters = this.filtersService.buildFilters(filterBuilderInput);
            return ResponseEntity.ok(filters);
        } catch (BIException exception) {
            log.error("Error while building Filter: " + exception.getMessage());
            return null;
        }
    }

    @PostMapping(value = "/remove", produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FiltersDTO> removeFilter(@RequestBody FilterBuilderInput input) {
        try {
            FiltersDTO filters = this.filtersService.removeFilter(input);
            return ResponseEntity.ok(filters);
        } catch (Exception exception) {
            log.error("Error while building Filter: " + exception.getMessage());
            return null;
        }
    }
}
