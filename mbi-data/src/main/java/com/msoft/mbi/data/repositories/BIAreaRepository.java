package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BIAreaRepository extends JpaRepository<BIAreaEntity, Integer> {
}
