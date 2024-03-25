package com.msoft.mbi.data.api.mapper.indicators.entities;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIIndAlertColorDTO;
import com.msoft.mbi.model.BIIndAlertColorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BIIndAlertColorMapper {

    BIIndAlertColorMapper BI_IND_ALERT_COLOR_MAPPER = Mappers.getMapper(BIIndAlertColorMapper.class);

    BIIndAlertColorDTO biEntityToDTO(BIIndAlertColorEntity entity);

    BIIndAlertColorEntity dtoToEntity(BIIndAlertColorDTO dto);

    Set<BIIndAlertColorDTO> setEntityToDTO(Set<BIIndAlertColorEntity> entities);

    Set<BIIndAlertColorEntity> setDTOToEntity(Set<BIIndAlertColorDTO> dtos);
}
