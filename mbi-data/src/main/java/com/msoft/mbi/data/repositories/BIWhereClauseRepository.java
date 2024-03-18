package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIWhereClauseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BIWhereClauseRepository extends JpaRepository<BIWhereClauseEntity, Integer> {
}
