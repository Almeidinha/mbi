package com.msoft.mbi.data.repositories;

import com.msoft.mbi.data.api.dtos.user.BIUserGroupDTO;
import com.msoft.mbi.model.BICompanyEntity;
import com.msoft.mbi.model.BIUserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BIUserGroupRepository extends JpaRepository<BIUserGroupEntity, Long> {


    //@Query("SELECT bug FROM BIUserGroupEntity bug JOIN bug.biCompanies c WHERE c.id = :companiId")
    List<BIUserGroupEntity> findByBiCompanies_Id(int companyId);

}
