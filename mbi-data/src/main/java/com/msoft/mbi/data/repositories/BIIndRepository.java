package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIIndEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BIIndRepository extends JpaRepository<BIIndEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE BIIndEntity SET usesSequence = :sequence WHERE id = :id")
    void changeSequence(@Param("id") Integer id, @Param("sequence") boolean sequence);
}
