package com.msoft.mbi.data.api.mapper.schedule;

import com.msoft.mbi.data.api.dtos.schedule.BIUserGroupScheduleDTO;
import com.msoft.mbi.model.BIUserGroupScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserGroupScheduleMapper {

    BIUserGroupScheduleMapper BI_USER_GROUP_SCHEDULE_MAPPER = Mappers.getMapper(BIUserGroupScheduleMapper.class);

    BIUserGroupScheduleDTO entityToDTO(BIUserGroupScheduleEntity entity);

    BIUserGroupScheduleEntity dtoToEntity(BIUserGroupScheduleDTO dto);

    Set<BIUserGroupScheduleDTO> setEntityToDTO(Set<BIUserGroupScheduleEntity> entities);

    Set<BIUserGroupScheduleEntity> dtoToSetEntity(Set<BIUserGroupScheduleDTO> dtos);
}
