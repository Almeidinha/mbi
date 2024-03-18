package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.user.BIUserGroupDTO;
import com.msoft.mbi.data.services.BIUserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class BIUserGroupController {

    private final BIUserGroupService userGroupService;


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserGroupDTO> getUserGroup(@PathVariable("id") Integer id) {
        BIUserGroupDTO userGroupDTO =  this.userGroupService.findById(id);

        return ResponseEntity.ok(userGroupDTO);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIUserGroupDTO>> getUserGroup() {
        List<BIUserGroupDTO> userGroupDTO = this.userGroupService.findByBiCompanies();

        return ResponseEntity.ok(userGroupDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BIUserGroupDTO create(@RequestBody BIUserGroupDTO userGroupDTO){
        return this.userGroupService.save(userGroupDTO);
    }

    @PutMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public BIUserGroupDTO update(@PathVariable Integer id, @RequestBody BIUserGroupDTO userGroupDTO){
        return userGroupService.update(id, userGroupDTO);
    }

    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public void  delete(@PathVariable Integer id) {
        userGroupService.deleteById(id);
    }

}
