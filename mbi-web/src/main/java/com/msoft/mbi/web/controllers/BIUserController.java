package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import com.msoft.mbi.data.services.BIUserService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class BIUserController {

    private final BIUserService userService;

    @GetMapping("/email/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> getUserByMail(@PathParam("email") String email) {
        BIUserDTO biUserDTO =  this.userService.findByEmail(email);

        if (biUserDTO != null) {
            return ResponseEntity.ok(biUserDTO);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> getUserById(@PathVariable("id") Integer id) {
        BIUserDTO biUserDTO =  this.userService.findById(id);

        if (biUserDTO != null) {
            return ResponseEntity.ok(biUserDTO);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIUserDTO>> getUserList() {
        List<BIUserDTO> biUserDTO =  this.userService.findAll();
        return ResponseEntity.ok(biUserDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BIUserDTO> create(@RequestHeader("userEmail") String email, @RequestBody BIUserDTO userDTO){
        return ResponseEntity.ok(userService.save(userDTO));
    }

    @PutMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> update(@PathVariable Integer id, @RequestBody BIUserDTO userDTO){
        BIUserDTO responseDto = userService.update(id, userDTO);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @PatchMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> patch(@PathVariable Integer id, @RequestBody BIUserDTO userDTO){
        BIUserDTO responseDto = userService.patch(id, userDTO);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public void  delete(@PathVariable Integer id) {
        userService.deleteById(id);
    }


}
