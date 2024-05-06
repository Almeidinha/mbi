package com.msoft.mbi.data.api.dtos.indicators;

import java.util.List;

public interface BIIndSummary {
    Integer getId();
    String getName();

    Area getBiAreaByArea();

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

    interface Area {
        Integer getId();
        String getDescription();
    }

}
