package com.msoft.mbi.data.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String authToken;
    private String email;
    private String error;
    private HttpStatus httpStatus;
    private int errorCode;
}
