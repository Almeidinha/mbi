package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIDimensionFilterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DimensionFilterRepository extends JpaRepository<BIDimensionFilterEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE BIDimensionFilterEntity SET sqlText = :sqlText " +
            "WHERE id = (SELECT biDimensionFilter.id FROM  BIIndEntity WHERE id = :id )")
    void updateRaw(@Param("id") Integer id, @Param("sqlText") String sqlText);

}
