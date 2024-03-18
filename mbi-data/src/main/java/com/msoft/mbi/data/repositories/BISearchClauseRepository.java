package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BISearchClauseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BISearchClauseRepository extends JpaRepository<BISearchClauseEntity, Integer> {
}
