package com.msoft.mbi.data.repositories;

import com.msoft.mbi.data.api.dtos.indicators.BIIndInfoDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIIndSummary;
import com.msoft.mbi.model.BIIndEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface BIIndRepository extends JpaRepository<BIIndEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE BIIndEntity SET usesSequence = :sequence WHERE id = :id")
    void changeSequence(@Param("id") Integer id, @Param("sequence") boolean sequence);

    @Query(value = "SELECT new com.msoft.mbi.data.api.dtos.indicators.BIIndInfoDTO(bi.id, bi.companyIdByCompany.id, bi.name, ba.id, ba.description) " +
            " FROM BIIndEntity bi " +
            " JOIN BIAreaEntity ba ON bi.biAreaByArea.id = ba.id ")
    List<BIIndInfoDTO> getIndicatorListDescription();

    <T> List<T> findBy(Class<T> type);

}
