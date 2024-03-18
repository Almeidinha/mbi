package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BIUserRepository extends JpaRepository<BIUserEntity, Long> {
    Optional<BIUserEntity> findByEmail(String email);

    @Query("SELECT id FROM BIUserEntity WHERE email = :email")
    int findUseridByEmail(@Param("email") String email);
}
