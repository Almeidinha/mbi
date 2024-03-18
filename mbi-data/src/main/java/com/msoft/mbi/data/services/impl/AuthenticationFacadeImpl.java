package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.services.AuthenticationFacade;
import com.msoft.mbi.data.services.BICompanyService;
import com.msoft.mbi.data.services.BIUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }




}
