package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.repositories.BIUserGroupIndRepository;
import com.msoft.mbi.data.services.BIUserGroupIndService;
import com.msoft.mbi.model.BIUserGroupIndEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BIUserGroupIndServiceImpl implements BIUserGroupIndService {

    private final BIUserGroupIndRepository uerGroupIndRepository;

    @Override
    public List<BIUserGroupIndEntity> findAll() {
        return null;
    }

    @Override
    public BIUserGroupIndEntity findById(Integer integer) {
        return null;
    }

    @Override
    public BIUserGroupIndEntity save(BIUserGroupIndEntity object) {
        return this.uerGroupIndRepository.save(object);
    }

    @Override
    public BIUserGroupIndEntity update(Integer integer, BIUserGroupIndEntity object) {
        return null;
    }

    @Override
    public void delete(BIUserGroupIndEntity object) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void saveAll(Iterable<BIUserGroupIndEntity> userGroupIndicatorEntities) {
        this.uerGroupIndRepository.saveAll(userGroupIndicatorEntities);
    }

    @Override
    public void deleteAll(Iterable<BIUserGroupIndEntity> userGroupIndicatorEntities) {
        this.uerGroupIndRepository.deleteAll(userGroupIndicatorEntities);
    }

    @Override
    public void deleteByIndicatorId(Integer indicatorId) {
        this.uerGroupIndRepository.deleteByIndicatorId(indicatorId);
    }
}
