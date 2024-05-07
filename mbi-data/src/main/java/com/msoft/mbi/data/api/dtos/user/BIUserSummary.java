package com.msoft.mbi.data.api.dtos.user;

public interface BIUserSummary {

        String getEmail();
        String getPassword();
        boolean getIsActive();

        UserGroup getBiUserGroupByUserGroup();

        interface UserGroup {
            String getRoleCode();
        }

}
