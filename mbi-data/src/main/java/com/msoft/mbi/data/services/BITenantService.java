package com.msoft.mbi.data.services;

import com.msoft.mbi.model.BITenantEntity;

import java.util.Set;
import java.util.UUID;

public interface BITenantService {

    Set<BITenantEntity> findAll();

    BITenantEntity findById(UUID id);

    BITenantEntity save(BITenantEntity biTenant);

    BITenantEntity update(UUID id, BITenantEntity biTenant);

    BITenantEntity patch(UUID id, BITenantEntity biTenant);

    void delete(BITenantEntity biTenant);

    void deleteById(UUID id);
}
