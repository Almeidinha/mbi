package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIFromClauseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BIbiFromClauseRepository extends JpaRepository<BIFromClauseEntity, Integer> {
}
