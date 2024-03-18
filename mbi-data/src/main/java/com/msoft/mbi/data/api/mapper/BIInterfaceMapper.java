package com.msoft.mbi.data.api.mapper;

import com.msoft.mbi.data.api.dtos.BIInterfaceDTO;
import com.msoft.mbi.model.BIInterfaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIInterfaceMapper {

    BIInterfaceMapper BI_INTERFACE_MAPPER_MAPPER = Mappers.getMapper(BIInterfaceMapper.class);

    @Mappings({
            @Mapping(target = "biInterfaceActions", source = "biInterfaceActionsByInterfaces"),
            @Mapping(target = "biUserGroupInterfaces", source = "biUserGroupInterfacesByInterfaces"),
            @Mapping(target = "biAccessRestrictionInterfaces", source = "biAccessRestrictionInterfacesByInterfaces"),
            @Mapping(target = "biUserInterfaces", source = "biUserInterfacesByInterfaces"),
    })
    BIInterfaceDTO biInterfaceEntityToDTO(BIInterfaceEntity biInterfaceEntity);

    @Mappings({
            @Mapping(target = "biInterfaceActionsByInterfaces", source = "biInterfaceActions"),
            @Mapping(target = "biUserGroupInterfacesByInterfaces", source = "biUserGroupInterfaces"),
            @Mapping(target = "biAccessRestrictionInterfacesByInterfaces", source = "biAccessRestrictionInterfaces"),
            @Mapping(target = "biUserInterfacesByInterfaces", source = "biUserInterfaces"),
    })
    BIInterfaceEntity dtoToBIInterfaceEntity(BIInterfaceDTO biInterfaceDTO);

    Set<BIInterfaceDTO> setBIInterfaceDTO(Set<BIInterfaceEntity> biInterfaceEntities);

    Set<BIInterfaceEntity> dtoToSetBIInterfaceEntity(Set<BIInterfaceDTO> biInterfaceDTOS);
    
}
