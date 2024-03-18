package com.msoft.mbi.data.api.mapper.user;

import com.msoft.mbi.data.api.dtos.user.BIUserGroupInterfaceDTO;
import com.msoft.mbi.model.BIUserGroupInterfaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserGroupInterfaceMapper {


    BIUserGroupInterfaceMapper BI_USER_GROUP_INTERFACE_MAPPER = Mappers.getMapper(BIUserGroupInterfaceMapper.class);

    @Mappings({
            @Mapping(target = "biUserGroupDTO", source = "biUserGroupByUserGroup"),
            @Mapping(target = "biInterfaceDTO", source = "biInterfaceByInterface")
    })
    BIUserGroupInterfaceDTO biUserGroupInterfaceEntityToDTO(BIUserGroupInterfaceEntity biUserGroupInterface);

    @Mappings({
            @Mapping(target = "biUserGroupByUserGroup", source = "biUserGroupDTO"),
            @Mapping(target = "biInterfaceByInterface", source = "biInterfaceDTO")
    })
    BIUserGroupInterfaceEntity dtoToBIUserGroupInterfaceEntity(BIUserGroupInterfaceDTO biUserGroupInterfaceDTO);

    Set<BIUserGroupInterfaceDTO> setBIUserGroupInterfaceDTO(Set<BIUserGroupInterfaceEntity> biUserGroupInterfaceEntities);

    Set<BIUserGroupInterfaceEntity> dtoToSetBIUserGroupInterfaceEntity(Set<BIUserGroupInterfaceDTO> biUserGroupInterfaceDTOS);
}
