package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BICompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BICompanyRepository extends JpaRepository<BICompanyEntity, Long>  {

    @Query("SELECT bc.id FROM BICompanyEntity bc JOIN BIUserEntity bue on bue.biCompany.id = bc.id  AND bue.email = :email")
    int findCompanyIdByUserEmail(@Param("email") String email);

    @Query("SELECT bc FROM BICompanyEntity bc JOIN BIUserEntity bue on bue.biCompany.id = bc.id  AND bue.email = :email")
    BICompanyEntity findCompanyByUserEmail(@Param("email") String email);

}
