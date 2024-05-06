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
        return this.userIndRepository.findAll();
    }

    @Override
    public BIUserIndEntity findById(Integer id) {
        return this.userIndRepository.findById(id).orElse(null);
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
    public void delete(BIUserIndEntity entity) {
        this.userIndRepository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {
        this.userIndRepository.deleteById(id);
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
                    .favorite(true).build();

            this.userIndRepository.save(userIndEntity);
            return;
        }

        this.userIndRepository.toggleIsFavorite(userId, indicatorId);
    }

    @Override
    public void toggleIsCanChange(Integer userId, Integer indicatorId) {
        this.userIndRepository.toggleIsCanChange(userId, indicatorId);
    }

    @Override
    public List<BIUserIndEntity> findAllByIndicatorId(Integer indicatorId) {
        return this.userIndRepository.findAllByIndicatorId(indicatorId);
    }


}
