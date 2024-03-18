package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIAnalysisFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BIAnalysisFieldRepository extends JpaRepository<BIAnalysisFieldEntity, Integer> {
}
