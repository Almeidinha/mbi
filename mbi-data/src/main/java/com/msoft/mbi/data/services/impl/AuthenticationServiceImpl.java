package com.msoft.mbi.data.services.impl;


import com.msoft.mbi.data.api.requests.SignUpRequest;
import com.msoft.mbi.data.api.requests.SigninRequest;
import com.msoft.mbi.data.api.response.JwtAuthenticationResponse;
import com.msoft.mbi.data.services.AuthenticationService;
import com.msoft.mbi.data.services.JwtService;
import com.msoft.mbi.data.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        /*var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();*/
        return null;
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        Authentication auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        try {
            final Authentication authentication = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails user = Optional.of(userService.loadUserByUsername(request.getEmail())).
                    orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

            String jwt = jwtService.generateToken(user);
            return JwtAuthenticationResponse.builder().authToken(jwt).email(user.getUsername()).build();
        } catch (AuthenticationException e) {
            return JwtAuthenticationResponse.builder().error(e.getMessage()).httpStatus(HttpStatus.FORBIDDEN).errorCode(HttpStatus.FORBIDDEN.value()).build();
        }

    }
}
