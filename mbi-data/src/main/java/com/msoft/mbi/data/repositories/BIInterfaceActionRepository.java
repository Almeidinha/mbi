package com.msoft.mbi.data.repositories;

import com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO;
import com.msoft.mbi.model.BIInterfaceActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BIInterfaceActionRepository extends JpaRepository<BIInterfaceActionEntity, Integer> {

    @Query(value = " SELECT new com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO(id, interfaceId, actionWeight, description) " +
            "FROM BIInterfaceActionEntity " +
            "WHERE interfaceId = :interfaceId " +
            "ORDER BY description")
    List<BIInterfaceActionDTO> loadInterfaceActions(@Param("interfaceId") Integer interfaceId);

}
