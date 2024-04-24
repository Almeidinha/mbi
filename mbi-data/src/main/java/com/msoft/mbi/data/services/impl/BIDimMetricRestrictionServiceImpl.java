package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionDTO;
import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionEntityDTO;
import com.msoft.mbi.data.repositories.BIDimMetricRestrictionRepository;
import com.msoft.mbi.data.services.BIDimMetricRestrictionService;
import com.msoft.mbi.model.BIDimMetricRestrictionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BIDimMetricRestrictionServiceImpl implements BIDimMetricRestrictionService {

    private final BIDimMetricRestrictionRepository repository;

    @Override
    public List<BIDimMetricRestrictionEntity> findAll() {
        return null;
    }

    @Override
    public BIDimMetricRestrictionEntity findById(Integer integer) {
        return null;
    }

    @Override
    public BIDimMetricRestrictionEntity save(BIDimMetricRestrictionEntity metricRestriction) {
        return this.repository.save(metricRestriction);
    }

    @Override
    public BIDimMetricRestrictionEntity update(Integer integer, BIDimMetricRestrictionEntity object) {
        return null;
    }

    @Override
    public void delete(BIDimMetricRestrictionEntity object) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public List<MetricDimensionRestrictionEntityDTO> findAllByIndicatorId(Integer indicatorId) {
        return this.repository.findAllByIndicatorId(indicatorId);
    }


    @Override
    public List<BIDimMetricRestrictionEntity> saveAll(List<BIDimMetricRestrictionEntity> restrictionEntities) {
        this.repository.deleteByIndicatorId(restrictionEntities.stream()
                .map(BIDimMetricRestrictionEntity::getIndicatorId)
                .collect(Collectors.toSet()));
        return this.repository.saveAll(restrictionEntities);
    }
}
