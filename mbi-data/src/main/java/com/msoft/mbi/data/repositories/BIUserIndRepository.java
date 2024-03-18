package com.msoft.mbi.data.repositories;


import com.msoft.mbi.model.BIUserIndEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BIUserIndRepository extends JpaRepository<BIUserIndEntity, Integer> {

    @Modifying
    @Query(value = "DELETE FROM BIUserIndEntity WHERE indicatorId = :indicatorId")
    void deleteByIndicatorId(@Param("indicatorId") Integer indicatorId);


    @Modifying
    @Transactional
    @Query(value = "UPDATE bi_user_indicator SET is_favorite = NOT is_favorite WHERE user_id = :userId AND indicator_id = :indicatorId", nativeQuery = true)
    void toggleIsFavorite(@Param("userId") Integer userId, @Param("indicatorId") Integer indicatorId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE bi_user_indicator SET can_change = NOT can_change WHERE user_id = :userId AND indicator_id = :indicatorId", nativeQuery = true)
    void toggleIsCanChange(@Param("userId") Integer userId, @Param("indicatorId") Integer indicatorId);

    boolean existsByUserIdAndIndicatorId(Integer userId, Integer indicatorId);

}
