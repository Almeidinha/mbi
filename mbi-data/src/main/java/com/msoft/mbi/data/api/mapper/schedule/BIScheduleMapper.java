package com.msoft.mbi.data.api.mapper.schedule;

import com.msoft.mbi.data.api.dtos.schedule.BIScheduleDTO;
import com.msoft.mbi.model.BIScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIScheduleMapper {

    BIScheduleMapper BI_SCHEDULE_MAPPER = Mappers.getMapper(BIScheduleMapper.class);

    BIScheduleDTO entityToDTO(BIScheduleEntity entity);

    BIScheduleEntity dtoToEntity(BIScheduleDTO dto);

    Set<BIScheduleDTO> setEntityToDTO(Set<BIScheduleEntity> entities);

    Set<BIScheduleEntity> dtoToSetEntity(Set<BIScheduleDTO> dtos);
}
