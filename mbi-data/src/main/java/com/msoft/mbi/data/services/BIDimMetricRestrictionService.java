package com.msoft.mbi.data.services;


import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionEntityDTO;
import com.msoft.mbi.model.BIDimMetricRestrictionEntity;

import java.util.List;

public interface BIDimMetricRestrictionService extends CrudService<BIDimMetricRestrictionEntity, Integer> {

    List<MetricDimensionRestrictionEntityDTO> findAllByIndicatorId(Integer indicatorId);

    List<BIDimMetricRestrictionEntity> saveAll(List<BIDimMetricRestrictionEntity> restrictionEntities);

    BIDimMetricRestrictionEntity save(BIDimMetricRestrictionEntity restrictionEntity);

}
