package com.msoft.mbi.data.api.mapper.user;

import com.msoft.mbi.data.api.dtos.user.BIUserInterfaceDTO;
import com.msoft.mbi.model.BIUserInterfaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserInterfaceMapper {

    BIUserInterfaceMapper BI_USER_INTERFACE_MAPPER = Mappers.getMapper(BIUserInterfaceMapper.class);

    @Mappings({
            @Mapping(target = "biUser", source = "biUserByUser"),
            @Mapping(target = "biInterface", source = "biInterfaceByInterface")
    })
    BIUserInterfaceDTO biUserInterfaceEntityToDTO(BIUserInterfaceEntity biUserInterfaceEntity);


    @Mappings({
            @Mapping(target = "biUserByUser", source = "biUser"),
            @Mapping(target = "biInterfaceByInterface", source = "biInterface")
    })
    BIUserInterfaceEntity dtoToBIUserInterfaceEntity(BIUserInterfaceDTO biUserInterfaceDTO);

    Set<BIUserInterfaceDTO> setBIUserInterfaceDTO(Set<BIUserInterfaceEntity> biUserInterfaceEntities);

    Set<BIUserInterfaceEntity> dtoToSetBIUserInterfaceEntity(Set<BIUserInterfaceDTO> biUserInterfaceDTOS);
    
}
