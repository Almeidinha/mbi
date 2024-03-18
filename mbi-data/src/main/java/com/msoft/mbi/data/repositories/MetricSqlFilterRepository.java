package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BISqlMetricFiltersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MetricSqlFilterRepository extends JpaRepository<BISqlMetricFiltersEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE BISqlMetricFiltersEntity SET sqlText = :sqlText " +
            "WHERE id = (SELECT biIndSqlMetricFilter.id FROM  BIIndEntity WHERE id = :id )")
    void updateRaw(@Param("id") Integer id, @Param("sqlText") String sqlText);
}
