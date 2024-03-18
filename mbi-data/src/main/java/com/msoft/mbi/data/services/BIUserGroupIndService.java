package com.msoft.mbi.data.services;

import com.msoft.mbi.model.BIUserGroupIndEntity;

public interface BIUserGroupIndService extends CrudService<BIUserGroupIndEntity, Integer> {

    void saveAll(Iterable<BIUserGroupIndEntity> userGroupIndicatorEntities);

    void deleteAll(Iterable<BIUserGroupIndEntity> userGroupIndicatorEntities);

    void deleteByIndicatorId(Integer indicatorId);

}
