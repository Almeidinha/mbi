package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.dtos.indicators.BIUserGroupIndDTO;
import com.msoft.mbi.model.BIUserGroupIndEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserGroupIndMapper {

    BIUserGroupIndMapper BI_USER_GROUP_IND_MAPPER = Mappers.getMapper(BIUserGroupIndMapper.class);

    BIUserGroupIndDTO biEntityToDTO(BIUserGroupIndEntity entity);

    BIUserGroupIndEntity dtoToEntity(BIUserGroupIndDTO dto);

    Set<BIUserGroupIndDTO> setEntityToDTO(Set<BIUserGroupIndEntity> entities);

    Set<BIUserGroupIndEntity> setDTOToEntity(Set<BIUserGroupIndDTO> dtos);
}
