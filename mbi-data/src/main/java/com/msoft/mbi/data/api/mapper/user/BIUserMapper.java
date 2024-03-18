package com.msoft.mbi.data.api.mapper.user;

import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import com.msoft.mbi.model.BIUserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserMapper {

    BIUserMapper BI_USER_MAPPER = Mappers.getMapper(BIUserMapper.class);

    @Mappings({
            @Mapping(target = "biCompanyId", source = "biCompany.id"),
            @Mapping(target = "userGroup", source = "biUserGroupByUserGroup"),
            @Mapping(target = "userInterface", source = "biUserInterfacesByUser"),
            @Mapping(target = "accessRestrictionInterface", source = "biAccessRestrictionInterfacesByUser"),
            @Mapping(target = "isAdmin", expression = "java(biUser.getAdmin())")
    })
    BIUserDTO biUserEntityToDTO(BIUserEntity biUser);


    @InheritInverseConfiguration
    BIUserEntity dtoToBIUserEntity(BIUserDTO biUserDTO);

    Set<BIUserDTO> setBIUserDTO(Set<BIUserEntity> biUserEntities);

    Set<BIUserEntity> dtoToSetBIUserEntity(Set<BIUserDTO> biUserDTOS);

}
