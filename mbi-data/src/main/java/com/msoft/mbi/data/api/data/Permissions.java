package com.msoft.mbi.data.api.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class Permissions {

    private int groupId;
    private int userId;
    private HashMap<String, Permission> groupListPermissions;
    private HashMap<String, Permission> userListPermission;
    private List<Permission> groupPermissions;
    private List<Permission> userPermissions;
    private boolean isGroupPermission;

    public Permissions(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
        loadPermissions();
    }

    public Permissions(int userId) {
        this(userId, 0);
    }

    private void loadPermissions() {

    }
}
