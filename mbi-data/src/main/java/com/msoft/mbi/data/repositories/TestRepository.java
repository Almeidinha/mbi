package com.msoft.mbi.data.repositories;

import com.msoft.mbi.model.BIUserIndEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<BIUserIndEntity, Integer> {

    @Query(
            value = "select * from bi_user_interface",
            nativeQuery = true)
    List<Object> getObjects();

}
