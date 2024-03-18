package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.repositories.BITenantRepository;
import com.msoft.mbi.data.services.BICompanyService;
import com.msoft.mbi.data.services.BITenantService;
import com.msoft.mbi.model.BITenantEntity;
import com.msoft.mbi.model.BIUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BITenantServiceImpl implements BITenantService {

    private final BITenantRepository tenantRepository;
    private final BICompanyService companyService;

    @Override
    public Set<BITenantEntity> findAll() {
        return new HashSet<>(tenantRepository.findAll());
    }

    @Override
    public BITenantEntity findById(UUID id) {
        return tenantRepository.findById(id).orElse(null);
    }

    @Override
    public BITenantEntity save(BITenantEntity biTenant) {
        int companyId = this.companyService.getCurrentUserCompanyId();
        biTenant.setCompanyId(companyId);
        return tenantRepository.save(biTenant);
    }

    @Override
    public BITenantEntity update(UUID id, BITenantEntity biTenant) {
        biTenant.setId(id);
        tenantRepository.save(biTenant);

        return biTenant;
    }

    @Override
    public BITenantEntity patch(UUID id, BITenantEntity biTenant) {
        Optional<BITenantEntity> biTenantEntity = tenantRepository.findById(id);

        if (biTenantEntity.isPresent()) {
            biTenantEntity.get().setConnectionName(biTenant.getConnectionName());
            biTenantEntity.get().setDatabaseTypeValue(biTenant.getDatabaseTypeValue());
            biTenantEntity.get().setUsername(biTenant.getUsername());
            biTenantEntity.get().setPassword(biTenant.getPassword());
            biTenantEntity.get().setUrl(biTenant.getUrl());

            return tenantRepository.save(biTenantEntity.get());
        }

        return null;
    }

    @Override
    public void delete(BITenantEntity biTenant) {
        tenantRepository.delete(biTenant);
    }

    @Override
    public void deleteById(UUID id) {
        tenantRepository.deleteById(id);
    }
}
