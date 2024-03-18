package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.requests.SignUpRequest;
import com.msoft.mbi.data.api.requests.SigninRequest;
import com.msoft.mbi.data.api.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);
}
