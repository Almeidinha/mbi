package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.data.GroupPermission;
import com.msoft.mbi.data.api.data.UserPermission;
import com.msoft.mbi.data.repositories.BIUserGroupPermissionsRepository;
import com.msoft.mbi.data.services.BIUserGroupPermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BIUserGroupPermissionsServiceImpl implements BIUserGroupPermissionsService {

    private final BIUserGroupPermissionsRepository biUserGroupPermissionsRepository;

    @Override
    public List<GroupPermission> loadGroupPermission(Integer groupId, Integer userId) {
        return biUserGroupPermissionsRepository.loadGroupPermission(groupId, userId);
    }

    @Override
    public List<UserPermission> loadUserPermission(Integer userId) {
        return biUserGroupPermissionsRepository.loadUserPermission(userId);
    }
}
