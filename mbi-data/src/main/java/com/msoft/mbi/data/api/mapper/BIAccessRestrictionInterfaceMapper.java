package com.msoft.mbi.data.api.mapper;

import com.msoft.mbi.data.api.dtos.BIAccessRestrictionInterfaceDTO;
import com.msoft.mbi.model.BIAccessRestrictionInterfaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIAccessRestrictionInterfaceMapper {

    BIAccessRestrictionInterfaceMapper BI_ACCESS_RESTRICTION_INTERFACE_MAPPER = Mappers.getMapper(BIAccessRestrictionInterfaceMapper.class);

    @Mappings({
            @Mapping(target = "biInterfaceDTO", source = "biInterfaceByInterface"),
            @Mapping(target = "biUserDTO", source = "biUserByUser")
    })
    BIAccessRestrictionInterfaceDTO biAccessRestrictionInterfaceEntityToDTO(BIAccessRestrictionInterfaceEntity biAccessRestrictionInterfaceEntity);

    @Mappings({
            @Mapping(target = "biInterfaceByInterface", source = "biInterfaceDTO"),
            @Mapping(target = "biUserByUser", source = "biUserDTO")
    })
    BIAccessRestrictionInterfaceEntity dtoToBIAccessRestrictionInterfaceEntity(BIAccessRestrictionInterfaceDTO biAccessRestrictionInterfaceDTO);

    Set<BIAccessRestrictionInterfaceDTO> setBIAccessRestrictionInterfaceDTO(Set<BIAccessRestrictionInterfaceEntity> biAccessRestrictionInterfaceEntities);

    Set<BIAccessRestrictionInterfaceEntity> dtoToSetBIAccessRestrictionInterfaceEntity(Set<BIAccessRestrictionInterfaceDTO> biAccessRestrictionInterfaceDTOS);
    
}
