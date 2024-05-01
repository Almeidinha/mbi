package com.msoft.mbi.data.services.impl;

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
        return repository.findAll();
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
    public void delete(BIDimMetricRestrictionEntity entity) {
        this.repository.delete(entity);
    }

    @Override
    public void deleteById(Integer integer) {
        this.repository.deleteById(integer);
    }

    @Override
    public List<MetricDimensionRestrictionEntityDTO> findAllByIndicatorId(Integer indicatorId) {
        return this.repository.findAllByIndicatorId(indicatorId);
    }


    @Override
    public List<BIDimMetricRestrictionEntity> saveAll(List<BIDimMetricRestrictionEntity> restrictionEntities) {
        this.deleteAll(restrictionEntities);
        return this.repository.saveAll(restrictionEntities);
    }

    @Override
    public void deleteAll(List<BIDimMetricRestrictionEntity> restrictionEntities) {
        this.repository.deleteByIndicatorIds(restrictionEntities.stream()
                .map(BIDimMetricRestrictionEntity::getIndicatorId)
                .collect(Collectors.toSet()));
    }

    @Override
    public void deleteByIndicator(Integer indicatorId) {
        this.repository.deleteByIndicatorId(indicatorId);
    }

    @Override
    public void deleteByMetric(Integer indicatorId, Integer metricId) {
        this.repository.deleteByIndicatorIdAndMetricId(indicatorId, metricId);
    }

    @Override
    public void deleteByDimension(Integer indicatorId, Integer metricId, Integer dimensionId) {
        this.repository.deleteByIndicatorIdAndMetricIdAndDimensionId(indicatorId, metricId, dimensionId);
    }

}
