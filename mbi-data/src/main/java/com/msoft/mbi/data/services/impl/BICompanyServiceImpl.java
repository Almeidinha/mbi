package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.repositories.BICompanyRepository;
import com.msoft.mbi.data.services.AuthenticationFacade;
import com.msoft.mbi.data.services.BICompanyService;
import com.msoft.mbi.data.services.BIUserService;
import com.msoft.mbi.model.BICompanyEntity;
import com.msoft.mbi.model.BIUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BICompanyServiceImpl implements BICompanyService {

    private final BICompanyRepository companyRepository;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public int findCompanyIdByUserEmail(String email) {
        return this.companyRepository.findCompanyIdByUserEmail(email);
    }

    @Override
    public List<BICompanyEntity> findAll() {
        return null;
    }

    @Override
    public BICompanyEntity findById(Integer integer) {
        return null;
    }

    @Override
    public BICompanyEntity save(BICompanyEntity object) {
        return null;
    }

    @Override
    public BICompanyEntity update(Integer integer, BICompanyEntity object) {
        return null;
    }

    @Override
    public void delete(BICompanyEntity object) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public BICompanyEntity getCurrentUserCompany() {
        Authentication authentication = this.authenticationFacade.getAuthentication();
        String email = authentication.getName();
        return this.companyRepository.findCompanyByUserEmail(email);
    }



    @Override
    public int getCurrentUserCompanyId() {
        Authentication authentication = this.authenticationFacade.getAuthentication();
        String email = authentication.getName();

        return this.findCompanyIdByUserEmail(email);
    }
}
