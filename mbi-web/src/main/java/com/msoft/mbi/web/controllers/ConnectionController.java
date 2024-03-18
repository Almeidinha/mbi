package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.services.BITenantService;
import com.msoft.mbi.model.BITenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/connection")
@RequiredArgsConstructor
public class ConnectionController {

    private final BITenantService biTenantService;

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BITenantEntity>> getConnections() {
       List<BITenantEntity> biTenantEntities = biTenantService.findAll().stream().toList();

       return ResponseEntity.ok(biTenantEntities);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BITenantEntity> getConnection(@PathVariable("id") String id) {
        BITenantEntity biTenant = biTenantService.findById(UUID.fromString(id));

        return ResponseEntity.ok(biTenant);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BITenantEntity> create(@RequestBody BITenantEntity biTenant){
        return ResponseEntity.ok(biTenantService.save(biTenant));
    }

    @PutMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BITenantEntity> update(@PathVariable String id, @RequestBody BITenantEntity biTenant){
        BITenantEntity update = biTenantService.update(UUID.fromString(id), biTenant);
        if (update != null) {
            return ResponseEntity.ok(update);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @PatchMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BITenantEntity> patch(@PathVariable String id, @RequestBody BITenantEntity biTenant){
        BITenantEntity patch = biTenantService.patch(UUID.fromString(id), biTenant);
        if (patch != null) {
            return ResponseEntity.ok(patch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public void  delete(@PathVariable String id) {
        biTenantService.deleteById(UUID.fromString(id));
    }
}
