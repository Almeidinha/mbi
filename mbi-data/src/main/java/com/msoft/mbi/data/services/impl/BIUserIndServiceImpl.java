package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.repositories.BIUserIndRepository;
import com.msoft.mbi.data.services.BIUserIndService;
import com.msoft.mbi.model.BIUserIndEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BIUserIndServiceImpl implements BIUserIndService {

    private final BIUserIndRepository userIndRepository;

    @Override
    public List<BIUserIndEntity> findAll() {
        return null;
    }

    @Override
    public BIUserIndEntity findById(Integer integer) {
        return null;
    }

    @Override
    public BIUserIndEntity save(BIUserIndEntity object) {
        return this.userIndRepository.save(object);
    }

    @Override
    public BIUserIndEntity update(Integer integer, BIUserIndEntity object) {
        return null;
    }

    @Override
    public void delete(BIUserIndEntity object) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void saveAll(Iterable<BIUserIndEntity> userIndEntities) {
        this.userIndRepository.saveAll(userIndEntities);
    }

    @Override
    public void deleteAll(Iterable<BIUserIndEntity> userIndEntities) {
        this.userIndRepository.deleteAll(userIndEntities);
    }

    @Override
    public void deleteByIndicatorId(Integer indicatorId) {
        this.userIndRepository.deleteByIndicatorId(indicatorId);
    }

    @Override
    public void toggleIsFavorite(Integer userId, Integer indicatorId) {

        boolean exist = this.userIndRepository.existsByUserIdAndIndicatorId(userId, indicatorId);

        if (!exist) {
            BIUserIndEntity userIndEntity = BIUserIndEntity.builder()
                    .userId(userId)
                    .indicatorId(indicatorId)
                    .canChange(false)
                    .isFavorite(true).build();

            this.userIndRepository.save(userIndEntity);
            return;
        }

        this.userIndRepository.toggleIsFavorite(userId, indicatorId);
    }

    @Override
    public void toggleIsCanChange(Integer userId, Integer indicatorId) {
        this.userIndRepository.toggleIsCanChange(userId, indicatorId);
    }
}
