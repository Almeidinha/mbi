package com.msoft.mbi.data.api.dtos.indicators;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface BIIndSummary {
    Integer getId();
    String getName();
    @Value("#{target.biAreaByArea.id}")
    Integer getAreaId();

    @Value("#{target.biAreaByArea.description}")
    String getAreaName();

    List<UserIndicators> getBiUserIndicators();

    List<UserGroupIndicators> getBiUserGroupIndicators();

    interface UserIndicators {
        Integer getUserId();
        boolean isFavorite();
        boolean isCanChange();
    }

    interface UserGroupIndicators {
        Integer getUserGroupId();
        boolean isCanEdit();
    }

}
