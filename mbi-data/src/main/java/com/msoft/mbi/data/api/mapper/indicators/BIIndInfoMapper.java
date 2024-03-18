package com.msoft.mbi.data.api.mapper.indicators;


import com.msoft.mbi.data.api.dtos.indicators.BIIndInfoDTO;
import com.msoft.mbi.model.BIIndEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIIndInfoMapper {

    BIIndInfoMapper BI_IND_MAPPER = Mappers.getMapper(BIIndInfoMapper.class);

    @Mappings({
            @Mapping(target = "areaName", source = "biAreaByArea.description"),
            @Mapping(target = "areaId", source = "biAreaByArea.id"),
            @Mapping(target = "companyId", source = "companyIdByCompany.id"),
            @Mapping(target = "biUserIndDtoList", source = "biUserIndicators"),
            @Mapping(target = "biUserGroupIndDtoList", source = "biUserGroupIndicators")
    })
    BIIndInfoDTO biEntityToDTO(BIIndEntity entity);

    @Mappings({
            @Mapping(target = "biUserIndicators", source = "biUserIndDtoList"),
            @Mapping(target = "biUserGroupIndicators", source = "biUserGroupIndDtoList")
    })
    BIIndEntity dtoToEntity(BIIndInfoDTO dto);

    Set<BIIndInfoDTO> setEntityToDTO(Set<BIIndEntity> entities);

    Set<BIIndEntity> setDTOToEntity(Set<BIIndInfoDTO> dtos);

}
