package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.data.GroupPermission;
import com.msoft.mbi.data.api.data.UserPermission;

import java.util.List;

public interface BIUserGroupPermissionsService {
    List<GroupPermission> loadGroupPermission(Integer groupId, Integer userId);

    List<UserPermission> loadUserPermission(Integer userId);
}
