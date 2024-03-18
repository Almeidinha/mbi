package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.services.BIUserIndService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-ind")
@RequiredArgsConstructor
public class BIUserIndController {

    private final BIUserIndService biUserIndService;

    @PostMapping("toggle-favorite/{userId}/{indicatorId}")
    @ResponseStatus(HttpStatus.OK)
    public void toggleIsFavorite(@PathVariable Integer userId, @PathVariable Integer indicatorId){
        this.biUserIndService.toggleIsFavorite(userId, indicatorId);
    }

    @PostMapping("toggle-can-change/{userId}/{indicatorId}")
    @ResponseStatus(HttpStatus.OK)
    public void toggleIsCanChange(@PathVariable Integer userId, @PathVariable Integer indicatorId){
        this.biUserIndService.toggleIsCanChange(userId, indicatorId);
    }
}
