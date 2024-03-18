package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.services.BIAreaService;
import com.msoft.mbi.model.BIAreaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/area")
@RequiredArgsConstructor
public class BIAreaController {

    private final BIAreaService areaService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIAreaEntity> getArea(@PathVariable("id") Integer id) {
        BIAreaEntity biArea =  this.areaService.findById(id);

        return ResponseEntity.ok(biArea);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIAreaEntity>> getAreas() {
        List<BIAreaEntity> biAreas = this.areaService.findAll();

        return ResponseEntity.ok(biAreas);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BIAreaEntity> create(@RequestBody BIAreaEntity biArea){
        return ResponseEntity.ok(this.areaService.save(biArea));
    }

    @PutMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIAreaEntity> update(@PathVariable Integer id, @RequestBody BIAreaEntity biArea){
        BIAreaEntity biAreaEntity = areaService.update(id, biArea);

        if (biAreaEntity != null) {
            return ResponseEntity.ok(biAreaEntity);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public void  delete(@PathVariable Integer id) {
        areaService.deleteById(id);
    }
}
