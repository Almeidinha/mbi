package com.msoft.mbi.data.api.data.indicator;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.IntStream;

@Setter
@Getter
public class MetricDimensionRestriction {

    private Integer metricId;
    private List<Integer> dimensionIds;

    public MetricDimensionRestriction(Integer id) {
        this.metricId = id;
    }

    public boolean isRestrictDimension(int dimensionId) {
       return dimensionIds.contains(dimensionId);
    }


    public boolean isRestrictMetric(Indicator indicator, int lastLevelCode) {
        if (lastLevelCode == -1) {
            return false;
        }

        return this.getDimensionIds().stream()
                .filter(dimensionId -> dimensionId == lastLevelCode)
                .anyMatch(dimensionId -> {
                    Field indField = indicator.getFieldByCode(String.valueOf(dimensionId));
                    return indField != null && "S".equals(indField.getDefaultField());
                });
    }
}
