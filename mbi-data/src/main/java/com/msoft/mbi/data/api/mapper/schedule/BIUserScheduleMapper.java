package com.msoft.mbi.data.api.mapper.schedule;

import com.msoft.mbi.data.api.dtos.schedule.BIUserScheduleDTO;
import com.msoft.mbi.model.BIUserScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIUserScheduleMapper {

    BIUserScheduleMapper BI_SCHEDULE_USER_MAPPER = Mappers.getMapper(BIUserScheduleMapper.class);

    BIUserScheduleDTO entityToDTO(BIUserScheduleEntity entity);

    BIUserScheduleEntity dtoToEntity(BIUserScheduleDTO dto);

    Set<BIUserScheduleDTO> setEntityToDTO(Set<BIUserScheduleEntity> entities);

    Set<BIUserScheduleEntity> dtoToSetEntity(Set<BIUserScheduleDTO> dtos);
}
