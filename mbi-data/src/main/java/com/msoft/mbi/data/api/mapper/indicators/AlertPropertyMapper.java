package com.msoft.mbi.data.api.mapper.indicators;

import com.msoft.mbi.data.api.data.indicator.AlertProperty;
import com.msoft.mbi.data.api.dtos.filters.OperatorDTO;
import com.msoft.mbi.data.api.dtos.indicators.AlertPropertyDTO;
import com.msoft.mbi.data.api.dtos.indicators.FieldDTO;
import com.msoft.mbi.data.api.mapper.filters.OperatorMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FieldMapper.class, OperatorMapper.class})
public interface AlertPropertyMapper {

    AlertPropertyMapper ALERT_PROPERTY_MAPPER = Mappers.getMapper(AlertPropertyMapper.class);

    AlertPropertyDTO alertPropertyToDTO(AlertProperty alertProperty);

    AlertProperty dtoToAlertProperty(AlertPropertyDTO dto);

    List<AlertPropertyDTO> alertPropertyListToDTO(List<AlertProperty> alertProperties);

    List<AlertProperty> listDTOToAlertPropertyList(List<AlertPropertyDTO> dtos);
}
