package com.msoft.mbi.web.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    // MultiTenantManager tenantManager;

    private static final String MSG_INVALID_TENANT_ID = "[!] DataSource not found for given tenant Id '{}'!";
    private static final String MSG_INVALID_DB_PROPERTIES_ID = "[!] DataSource properties related to the given tenant ('{}') is invalid!";
    private static final String MSG_RESOLVING_TENANT_ID = "[!] Could not resolve tenant ID '{}'!";

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Object>> getObjects() {
        // setTenant("1");
        List<Object> objects =  this.testService.getObjects();
        return ResponseEntity.ok(objects);
    }

    @GetMapping("/table/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getTable(@PathVariable Integer id) {

        String table = testService.getTable(id);

        return ResponseEntity.ok(table);
    }

    @GetMapping("/table/json/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ObjectNode> getJsonTable(@PathVariable Integer id) {

        ObjectNode table = testService.getJsonTable(id);

        return ResponseEntity.ok(table);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Object>> getObAllObjects() {
        List<Object> objects =  this.testService.getObjects();
        return ResponseEntity.ok(objects);
    }

    @GetMapping("/tree/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getFilterTree(@PathVariable Integer id) throws BIException {
        String tree =  this.testService.getJsonTree(id);
        return ResponseEntity.ok(tree);
    }

    @GetMapping("/filters/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FiltersDTO> getFilters(@PathVariable Integer id) throws BIException {
        FiltersDTO dto =  this.testService.getFiltersDTO(id);
        return ResponseEntity.ok(dto);
    }

   /* private void setTenant(String tenantId) {
        try {
            tenantManager.setCurrentTenant(tenantId);
        } catch (SQLException e) {
            throw new InvalidDbPropertiesException();
        } catch (TenantNotFoundException e) {
            throw new InvalidTenantIdExeption();
        } catch (TenantResolvingException e) {
            throw new InvalidTenantIdExeption();
        }
    }*/

}
