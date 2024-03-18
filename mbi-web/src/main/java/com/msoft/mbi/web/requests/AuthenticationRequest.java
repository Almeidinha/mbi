package com.msoft.mbi.web.requests;

import lombok.Getter;
import org.springframework.web.bind.annotation.RestController;

@Getter
@RestController
public class AuthenticationRequest {

    private String email;
    private String password;

}
