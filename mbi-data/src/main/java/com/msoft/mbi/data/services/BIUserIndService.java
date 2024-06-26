package com.msoft.mbi.data.services;

import com.msoft.mbi.model.BIUserIndEntity;

import java.util.List;

public interface BIUserIndService extends CrudService<BIUserIndEntity, Integer> {

    void saveAll(Iterable<BIUserIndEntity> userIndEntities);

    void deleteAll(Iterable<BIUserIndEntity> userIndEntities);

    void deleteByIndicatorId(Integer indicatorId);

    void toggleIsFavorite(Integer userId, Integer indicatorId);

    void toggleIsCanChange(Integer userId, Integer indicatorId);

    List<BIUserIndEntity> findAllByIndicatorId(Integer indicatorId);
}
