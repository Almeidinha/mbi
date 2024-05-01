package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionEntityDTO;
import com.msoft.mbi.data.api.mapper.restrictions.MetricDimensionRestrictionEntityMapper;
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
    private final MetricDimensionRestrictionEntityMapper restrictionMapper;

    @GetMapping("{indicatorId}/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MetricDimensionRestrictionEntityDTO>> findAllByIndicatorId(@PathVariable Integer indicatorId) {
        List<MetricDimensionRestrictionEntityDTO> metricRestrictions = this.restrictionService.findAllByIndicatorId(indicatorId);
        return ResponseEntity.ok(metricRestrictions);
    }

    @PostMapping("/list/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MetricDimensionRestrictionEntityDTO>> saveAll(@RequestBody List<MetricDimensionRestrictionEntityDTO> restrictionEntities) {
        List<BIDimMetricRestrictionEntity> metricRestrictions = this.restrictionService.saveAll(restrictionMapper.listDTOToEntities(restrictionEntities));
        return ResponseEntity.ok(restrictionMapper.listEntityToDTOs(metricRestrictions));
    }

    @DeleteMapping("/list/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll(@RequestBody List<MetricDimensionRestrictionEntityDTO> restrictionEntities) {
        this.restrictionService.deleteAll(restrictionMapper.listDTOToEntities(restrictionEntities));
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MetricDimensionRestrictionEntityDTO> save(@RequestBody MetricDimensionRestrictionEntityDTO restrictionEntity) {
        BIDimMetricRestrictionEntity metricRestriction = this.restrictionService.save(restrictionMapper.dtoToEntity((restrictionEntity)));
        return ResponseEntity.ok(restrictionMapper.biEntityToDTO(metricRestriction));
    }

    @DeleteMapping({"/{indicatorId}"})
    @ResponseStatus(HttpStatus.OK)
    public void  deleteByIndicator(@PathVariable Integer indicatorId) {
        this.restrictionService.deleteByIndicator(indicatorId);
    }

    @DeleteMapping({"/{indicatorId}/{metricId}"})
    @ResponseStatus(HttpStatus.OK)
    public void  deleteByMetric(@PathVariable Integer indicatorId, @PathVariable Integer metricId) {
        this.restrictionService.deleteByMetric(indicatorId, metricId);
    }

    @DeleteMapping({"/{indicatorId}/{metricId}/{dimensionId}"})
    @ResponseStatus(HttpStatus.OK)
    public void  deleteByDimension(
            @PathVariable Integer indicatorId,
            @PathVariable Integer metricId,
            @PathVariable Integer dimensionId) {

        this.restrictionService.deleteByDimension(indicatorId, metricId, dimensionId);

    }
}
