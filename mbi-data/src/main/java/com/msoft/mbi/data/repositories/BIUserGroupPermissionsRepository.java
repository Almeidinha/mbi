package com.msoft.mbi.data.repositories;

import com.msoft.mbi.data.api.data.GroupPermission;
import com.msoft.mbi.data.api.data.UserPermission;
import com.msoft.mbi.model.BIUserGroupInterfaceEntity;
import com.msoft.mbi.model.BIUserInterfaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BIUserGroupPermissionsRepository  extends JpaRepository<BIUserGroupInterfaceEntity, Integer> {

    @Query("SELECT interfaceId, userGroupId, permissionLevel  FROM BIUserGroupInterfaceEntity " +
            "WHERE userGroupId = :userGroupId " +
            "AND interfaceId NOT IN (SELECT interfaceId from  BIAccessRestrictionInterfaceEntity " +
            "WHERE userGroupId = :groupId AND userId = :userId)")
    List<GroupPermission> loadGroupPermission(
            @Param("groupId") Integer groupId,
            @Param("userId") Integer userId
    );

    @Query("SELECT interfaceId, userId, permissionLevel  FROM BIUserInterfaceEntity WHERE userId = :userId ")
    List<UserPermission> loadUserPermission(@Param("userId") Integer userId);

}
