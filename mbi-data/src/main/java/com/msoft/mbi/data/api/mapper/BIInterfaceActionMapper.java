package com.msoft.mbi.data.api.mapper;

import com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO;
import com.msoft.mbi.model.BIInterfaceActionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIInterfaceActionMapper {


    BIInterfaceActionMapper BI_INTERFACE_ACTION_MAPPER = Mappers.getMapper(BIInterfaceActionMapper.class);

    @Mappings({
            @Mapping(target = "biInterface", source = "biInterfaceByInterface"),
    })
    BIInterfaceActionDTO biInterfaceActionEntityToDTO(BIInterfaceActionEntity biInterfaceActionEntity);

    @Mappings({
            @Mapping(target = "biInterfaceByInterface", source = "biInterface"),
    })
    BIInterfaceActionEntity dtoToBIInterfaceActionEntity(BIInterfaceActionDTO biInterfaceActionDTO);

    Set<BIInterfaceActionDTO> setBIInterfaceActionDTO(Set<BIInterfaceActionEntity> biInterfaceActionEntities);

    Set<BIInterfaceActionEntity> dtoToSetBIInterfaceActionEntity(Set<BIInterfaceActionDTO> biInterfaceActionDTOS);
}
