package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import com.msoft.mbi.data.api.dtos.user.BIUserSummary;
import com.msoft.mbi.data.services.BIUserService;
import com.msoft.mbi.data.services.UserService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class BIUserController {

    private final BIUserService biUserService;
    private final UserService userService;

    @GetMapping("/email/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> getUserByMail(@PathParam("email") String email) {
        BIUserDTO biUserDTO =  this.biUserService.findByEmail(email);

        if (biUserDTO != null) {
            return ResponseEntity.ok(biUserDTO);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /* TODO Create a new one that does not return password*/
    @GetMapping("/summary/email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserSummary> getUserSummaryByMail(@PathParam("email") String email) {
        Optional<BIUserSummary> biUserSummary =  this.userService.findUserSummaryByEmail(email);
        return biUserSummary.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> getUserById(@PathVariable("id") Integer id) {
        BIUserDTO biUserDTO =  this.biUserService.findById(id);

        if (biUserDTO != null) {
            return ResponseEntity.ok(biUserDTO);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BIUserDTO>> getUserList() {
        List<BIUserDTO> biUserDTO =  this.biUserService.findAll();
        return ResponseEntity.ok(biUserDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BIUserDTO> create(@RequestHeader("userEmail") String email, @RequestBody BIUserDTO userDTO){
        return ResponseEntity.ok(biUserService.save(userDTO));
    }

    @PutMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> update(@PathVariable Integer id, @RequestBody BIUserDTO userDTO){
        BIUserDTO responseDto = biUserService.update(id, userDTO);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @PatchMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BIUserDTO> patch(@PathVariable Integer id, @RequestBody BIUserDTO userDTO){
        BIUserDTO responseDto = biUserService.patch(id, userDTO);
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping({"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public void  delete(@PathVariable Integer id) {
        biUserService.deleteById(id);
    }


}
