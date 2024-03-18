package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIUserGroupIndEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BIUserGroupIndRepository extends JpaRepository<BIUserGroupIndEntity, Integer> {

    @Modifying
    @Query(value = "DELETE FROM BIUserGroupIndEntity WHERE indicatorId = :indicatorId")
    void deleteByIndicatorId(@Param("indicatorId") Integer indicatorId);
}
