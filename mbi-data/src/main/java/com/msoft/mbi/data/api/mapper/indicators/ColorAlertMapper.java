package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.data.indicator.ColorAlert;
import com.msoft.mbi.data.api.dtos.indicators.ColorAlertDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ColorAlertMapper {

    ColorAlertMapper COLOR_ALERT_MAPPER = Mappers.getMapper(ColorAlertMapper.class);

    ColorAlertDTO colorAlertToDTO(ColorAlert colorAlert);

    ColorAlert dtoToColorAlert(ColorAlertDTO dto);

    List<ColorAlertDTO> colorAlertListToDTO(List<ColorAlert> colorAlerts);

    List<ColorAlert> listDTOToColorAlertList(List<ColorAlertDTO> dtos);
}
