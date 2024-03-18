package com.msoft.mbi.data.repositories;

import com.msoft.mbi.data.api.dtos.BIInterfaceDTO;
import com.msoft.mbi.model.BIInterfaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BIInterfacesRepository extends JpaRepository<BIInterfaceEntity, Integer> {

    @Query(value = "SELECT new com.msoft.mbi.data.api.dtos.BIInterfaceDTO(id, name) FROM BIInterfaceEntity WHERE id = :interfaceId")
    BIInterfaceDTO loadInterface(@Param("interfaceId")  Integer interfaceId);

    @Query(value = "SELECT new com.msoft.mbi.data.api.dtos.BIInterfaceDTO(id, name) FROM BIInterfaceEntity")
    List<BIInterfaceDTO> loadInterfaces();

}
