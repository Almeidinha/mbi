package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.restrictions.BIMetricRestrictionDTO;
import com.msoft.mbi.data.api.mapper.restrictions.BIMetricRestrictionMapper;
import com.msoft.mbi.data.services.BIDimMetricRestrictionService;
import com.msoft.mbi.model.BIDimMetricRestrictionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metric/restrictions")
@RequiredArgsConstructor
public class BIDimMetricRestrictionController {

    private final BIDimMetricRestrictionService restrictionService;
    private final BIMetricRestrictionMapper restrictionMapper;

    @GetMapping("{indicatorId}/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIMetricRestrictionDTO>> findAllByIndicatorId(@PathVariable Integer indicatorId) {
        List<BIMetricRestrictionDTO> metricRestrictions = this.restrictionService.findAllByIndicatorId(indicatorId);
        return ResponseEntity.ok(metricRestrictions);
    }

    @PostMapping("/list/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIMetricRestrictionDTO>> saveAll(@RequestBody List<BIMetricRestrictionDTO> restrictionEntities) {
        List<BIDimMetricRestrictionEntity> metricRestrictions = this.restrictionService.saveAll(restrictionMapper.listDTOToEntities(restrictionEntities));
        return ResponseEntity.ok(restrictionMapper.listEntityToDTOs(metricRestrictions));
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIMetricRestrictionDTO> save(@RequestBody BIMetricRestrictionDTO restrictionEntity) {
        BIDimMetricRestrictionEntity metricRestriction = this.restrictionService.save(restrictionMapper.dtoToEntity((restrictionEntity)));
        return ResponseEntity.ok(restrictionMapper.biEntityToDTO(metricRestriction));
    }

    @DeleteMapping({"/{indicatorId}"})
    @ResponseStatus(HttpStatus.OK)
    public void  deleteByIndicator(@PathVariable Integer indicatorId) {

    }

    @DeleteMapping({"/{indicatorId}/{metricId}"})
    @ResponseStatus(HttpStatus.OK)
    public void  deleteByMetric(@PathVariable Integer indicatorId, @PathVariable Integer metricId) {

    }

    @DeleteMapping({"/{indicatorId}/{metricId}/{dimensionId}"})
    @ResponseStatus(HttpStatus.OK)
    public void  removeDimensionFormMetricRestriction(@PathVariable Integer indicatorId,
                                                      @PathVariable Integer metricId,
                                                      @PathVariable Integer dimensionId) {

    }
}
