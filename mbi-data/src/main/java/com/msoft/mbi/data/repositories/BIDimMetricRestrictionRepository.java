package com.msoft.mbi.data.repositories;

import com.msoft.mbi.data.api.dtos.restrictions.BIMetricRestrictionDTO;
import com.msoft.mbi.model.BIDimMetricRestrictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface BIDimMetricRestrictionRepository extends JpaRepository<BIDimMetricRestrictionEntity, Integer> {

    @Query(value = " SELECT new com.msoft.mbi.data.api.dtos.restrictions.BIMetricRestrictionDTO(indicatorId, metricId, dimensionId)" +
    "FROM BIDimMetricRestrictionEntity " +
    "WHERE indicatorId = :indicatorId")
    List<BIMetricRestrictionDTO> findAllByIndicatorId(@Param("indicatorId") Integer indicatorId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM BIDimMetricRestrictionEntity WHERE indicatorId IN :indicatorIds")
    void deleteByIndicatorId(@Param("indicatorIds") Set<Integer> indicatorIds);

}
