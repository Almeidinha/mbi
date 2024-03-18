package com.msoft.mbi.data.api.mapper.user;

import com.msoft.mbi.data.api.dtos.user.BIUserGroupDTO;
import com.msoft.mbi.model.BIUserGroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserGroupMapper {

    BIUserGroupMapper BI_USER_GROUP_MAPPER = Mappers.getMapper(BIUserGroupMapper.class);

    BIUserGroupDTO biUserGroupEntityToDTO(BIUserGroupEntity biUserGroupEntity);

    BIUserGroupEntity dtoToBIUserGroupEntity(BIUserGroupDTO biUserGroupDTO);

    Set<BIUserGroupDTO> setBIUserGroupDTO(Set<BIUserGroupEntity> biUserGroupEntities);

    Set<BIUserGroupEntity> dtoToSetBIUserGroupEntity(Set<BIUserGroupDTO> biUserGroupDTOS);

}
