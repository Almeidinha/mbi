package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BITenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BITenantRepository extends JpaRepository<BITenantEntity, UUID> {
}
