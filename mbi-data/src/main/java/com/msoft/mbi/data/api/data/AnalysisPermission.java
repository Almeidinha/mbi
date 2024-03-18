package com.msoft.mbi.data.api.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalysisPermission {

    PermissionType type;
    Integer userId;
    Integer groupId;
    PermissionLevel level;
    boolean isFavorite;
}
